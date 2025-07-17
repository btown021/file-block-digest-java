# file-block-digest-java

 因工作需求需要对文件对接企业微信微盘实现文件同步
企业微信微盘分块上传文档 [文件分块上传 - 文档 - 企业微信开发者中心](https://developer.work.weixin.qq.com/document/path/98004)

文件上传接口 sha 值, 用 java 实现计算会有问题 和他的原版demo https://github.com/wecomopen/file_block_digest 计算结果有差异
只能将其原版 C++ 代码编译为二进制工具运行

写了一个工具类 有需要的自行获取
Spring 环境可直接引入 `【curl C】`
需要`hutool`依赖



# 测试代码

```java
// 官方测试文件
ArrayList<String> blockSha = FileDigestSha.getBlockSha(new File("sha_calc_demo.txt"));

//结果
//5186fee37e9f7e77a1f6bea8d4e32638d5186d44
//cf540965f3190bc0c6367147ab0ac64dc9eff6a0
//bda1e884e1cb23a5cbdea73d2e00e23baa72d2f5

//与官方结果一致

```

## demo文件基本信息

文件大小 `5999998`字节。

通过 test_file_block_digest 工具输出：
part_num: 1 end_offset: 2097152 cumulate_sha1: 5186fee37e9f7e77a1f6bea8d4e32638d5186d44

part_num: 2 end_offset: 4194304 cumulate_sha1: cf540965f3190bc0c6367147ab0ac64dc9eff6a0

part_num: 3 end_offset: 5999998 cumulate_sha1: bda1e884e1cb23a5cbdea73d2e00e23baa72d2f5

表明需要分`3`块来上传，对应的 block_sha为：

5186fee37e9f7e77a1f6bea8d4e32638d5186d44

cf540965f3190bc0c6367147ab0ac64dc9eff6a0

bda1e884e1cb23a5cbdea73d2e00e23baa72d2f5



