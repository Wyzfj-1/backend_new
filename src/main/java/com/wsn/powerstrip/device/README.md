# device package

设备管理包

## 功能:

1. 设备控制
2. 设备详情
3. 设备上报数据推送

## 结构：

1. service：实现controller的逻辑
2. manager：service的更底层实现（来源于Alibaba java手册）

## 引用其他包(除common外):

1. member包,用于获得插排所处的组织
2. iotPlatform包, 用于控制插排,获得插排上报历史