# plusls carpet addition

这是一个 [Carpet mod](https://github.com/gnembon/fabric-carpet) (fabric-carpet) 的扩展 mod，包含了不少~~NotVanilla的~~有意思的功能以及特性

跟同 Minecraft 版本的 carpet mod 一起使用即可。尽可能地使用较新的 carpet mod

## 索引

### [规则](#规则列表)

- [PCA 同步协议](#PCA同步协议-pcaSyncProtocol)
- [空潜影盒可堆叠](#空潜影盒可堆叠-emptyShulkerBoxStack)
- [空潜影盒在容器中可堆叠](#空潜影盒在容器中可堆叠-emptyShulkerBoxStackInInventory)
- [PCA调试模式](#PCA调试模式-pcaDebug)

## 规则列表

### PCA同步协议 (pcaSyncProtocol)

plusls carpet addition sync protocol

PCA 同步协议是一个用于在服务端和客户端之间同步 Entity，BlockEntity 的协议，目前被 [MasaGadget](https://github.com/plusls/MasaGadget) 用于实现多人游戏容器预览。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `true`, `false`
- 分类: `PCA`, `protocal`

### 空潜影盒可堆叠 (emptyShulkerBoxStack)

Carpet 默认实现的潜影盒可堆叠只能让潜影盒在地面上堆叠，无法在背包中以及容器中手动堆叠

对于 CarpetExtra 实现的潜影盒可堆叠则过于激进，它会导致空潜影盒从漏斗进入箱子时会自动堆叠，这样一来会影响比较器的输出导致一些机器坏掉

因此额外实现了一次 emptyShulkerBoxStack，开启本功能后既能手动堆叠潜影盒，同时不会影响漏斗和比较器的逻辑

本功能无法让潜影盒在地面上堆叠，建议结合 Carpet 的潜影盒堆叠使用

本功能需要客户端支持潜影盒堆叠

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `true`, `false`
- 分类: `PCA`, `feature`

### 空潜影盒在容器中可堆叠 (emptyShulkerBoxStackInInventory)

该功能是 emptyShulkerBoxStack 的强化版，它会导致空潜影盒在容器中自动堆叠并影响比较器和漏斗的逻辑

本功能只有在 emptyShulkerBoxStack 时才能生效

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `true`, `false`
- 分类: `PCA`, `feature`

### PCA调试模式 (pcaDebug)

开启后会打印调试信息

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `true`, `false`
- 分类: `PCA`


## License

This template is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.
