import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.util.RuntimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * 文件摘要 SHA
 *
 * @author ycy
 * @date 2025/07/16
 */
@Slf4j
public class FileDigestSha {

    /**
     * 文件摘要计算的块大小（单位：字节）
     *
     * 重要说明：此值与二进制工具 `file-block-digest` 内部使用的块大小一致。
     */
    public static final int DIGEST_BLOCK_SIZE_BYTES = 2 * 1024 * 1024;

    private static String SOFTWARE_PATH;

    static {
            // 识别操作系统
            String osName = System.getProperty("os.name");

            String softwareName = null;

            // file-block-digest 计算sha值二级制工具 是从官方的 c++ demo 里 编译的
            // 选择对应平台的二进制工具
            if (osName == null || osName.startsWith("Linux")) {
                softwareName = "file-block-digest-linux";
            } else if (osName.startsWith("Mac")) {
                softwareName = "file-block-digest-mac";
            } else if (osName.startsWith("Win")) {
                softwareName = "file-block-digest-win.exe";
            }

            if (softwareName == null) {
                softwareName = "file-block-digest-linux";
            }

        // 加载二进制工具到本地
        try {
            // 从classpath加载
            // 直接获取文件
            File file = ResourceUtils.getFile("classpath:file-digest/" + softwareName);
            SOFTWARE_PATH = file.getAbsolutePath();
        } catch (Exception e) {
            // 文件获取失败 因为打包原因 文件不是file://协议 无法字节获取文件对象
            // 解决方法 获取文件流 写入当前目录
            File file = new File("file-digest/" + softwareName);
            // 文件已存在 避免重复写文件
            if (!FileUtil.exist(file)) {

                Resource resource = new ClassPathResource("classpath:file-digest/" + softwareName);
                FileUtil.writeFromStream(resource.getStream(), file);

                // 设置执行权限（非Windows系统）
                assert osName != null;
                if (!osName.startsWith("Win")) {
                    RuntimeUtil.exec("chmod +x " + file.getAbsoluteFile().getAbsolutePath());
                }
            }
            SOFTWARE_PATH = file.getAbsoluteFile().getAbsolutePath();
        }
    }


    /**
     * 获取
     *
     * @param file 文件
     * @return {@link ArrayList }<{@link String }> 有序
     */
    public static ArrayList<String> getBlockSha(File file) {
        try {
            // 构建命令：工具路径 + 文件绝对路径
            String cmd = String.format("%s %s", SOFTWARE_PATH, file.getAbsolutePath());
            // 执行命令并获取输出
            String res = RuntimeUtil.execForStr(cmd);
            // 解析输出结果
            ArrayList<String> blockSha = new ArrayList<>();
            for (String s : res.split("\n")) {
                if (s.contains("cumulate_sha1:")) {
                    // 提取累积SHA值
                    blockSha.add(s.split("cumulate_sha1:")[1].trim());
                }
            }

            return blockSha;
        } catch (Exception e) {
            log.error("文件计算BlockSha错误", e);
            throw new RuntimeException(e);
        }
    }
}
