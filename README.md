# plusls carpet addition

这是一个 [Carpet mod](https://github.com/gnembon/fabric-carpet) (fabric-carpet) 的扩展 mod，包含了不少~~NotVanilla的~~有意思的功能以及特性

跟同 Minecraft 版本的 carpet mod 一起使用即可。尽可能地使用较新的 carpet mod

## 依赖

fabric-api >= 0.28

## 索引

### [规则](#规则列表)

- [PCA 同步协议](#PCA同步协议-pcaSyncProtocol)
- [PCA 同步协议可同步玩家数据](#PCA同步协议可同步玩家数据-pcaSyncPlayerEntity)
- [空潜影盒可堆叠](#空潜影盒可堆叠-emptyShulkerBoxStack)
- [潜影贝可再生](#潜影贝可再生-shulkerRenewable)
- [潜影盒快速拆包](#潜影盒快速拆包-shulkerBoxQuickUnpack)
- [铁轨不被液体破坏](#铁轨不被液体破坏-railNoBrokenByFluid)
- [潜影盒使用染料染色](#潜影盒使用染料染色-useDyeOnShulkerBox)
- [不死图腾扳手](#不死图腾扳手-flippingTotemOfUndying)
- [刷怪的最大Y值](#刷怪的最大Y值-spawnYMax)
- [刷怪的最小Y值](#刷怪的最小Y值-spawnYMin)
- [全局刷怪群系](#全局刷怪群系-spawnBiome)
- [Xaero小地图世界名](#Xaero小地图世界名-xaeroWorldName)
- [PCA调试模式](#PCA调试模式-pcaDebug)

## 规则列表

### PCA同步协议 (pcaSyncProtocol)

plusls carpet addition sync protocol

PCA 同步协议是一个用于在服务端和客户端之间同步 Entity，BlockEntity 的协议，目前被 [MasaGadget](https://github.com/plusls/MasaGadget) 用于实现多人游戏容器预览。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `true`, `false`
- 分类: `PCA`, `protocal`

### PCA同步协议可同步玩家数据 (pcaSyncPlayerEntity)

该选项能决定哪些玩家的数据将会被 PCA 同步协议同步

NOBODY：所有玩家数据都无法同步

BOT：地毯 mod 召唤出的 bot 的数据可以被同步

OPS：地毯 mod 召唤出的 bot 的数据可以被同步， op 可以同步所有玩家的数据

OPS_AND_SELF：地毯 mod 召唤出的 bot 和玩家自己的数据可以被同步，op 可以同步所有玩家的数据

EVERYONE：所有人的数据都可以被同步

- 类型: `enum`
- 默认值: `OPS`
- 参考选项: `nobody`, `bot`, `ops`, `ops_and_self`, `everyone`
- 分类: `PCA`, `protocal`

### 空潜影盒可堆叠 (emptyShulkerBoxStack)

Carpet 默认实现的潜影盒可堆叠只能让潜影盒在地面上堆叠，无法在背包中以及容器中手动堆叠

对于 CarpetExtra 实现的潜影盒可堆叠则过于激进，它会导致空潜影盒从漏斗进入箱子时会自动堆叠，这样一来会影响比较器的输出导致一些机器坏掉

因此额外实现了一次 emptyShulkerBoxStack，开启本功能后既能手动堆叠潜影盒，同时不会影响漏斗和比较器的逻辑

本功能无法让潜影盒在地面上堆叠，建议结合 Carpet 的潜影盒堆叠使用

本功能需要客户端支持潜影盒堆叠，例如 tweakeroo

或者在客户端安装本 mod

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `true`, `false`
- 分类: `PCA`, `feature`

### 潜影贝可再生 (shulkerRenewable)

本功能移植自 1.17

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `true`, `false`
- 分类: `PCA`, `feature`, `experimental`

### 潜影盒快速拆包 (shulkerBoxQuickUnpack)

物品状态下的潜影盒在被摧毁时，盒中的物品会自动掉落，来自 1.17

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `true`, `false`
- 分类: `PCA`, `feature`, `experimental`

### 铁轨不被液体破坏 (railNoBrokenByFluid)

1.17 引入了含水铁轨，考虑到改动可能会比较大，因此只引入了其不被液体破坏的特性

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `true`, `false`
- 分类: `PCA`, `feature`, `experimental`

### 潜影盒使用染料染色 (useDyeOnShulkerBox)

可以使用染料直接对地上的潜影盒染色，用水瓶右键洗去地上潜影盒的颜色

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `true`, `false`
- 分类: `PCA`, `feature`

### 不死图腾扳手 (flippingTotemOfUndying)

允许使用不死图腾调整方块朝向，并且不会产生方块更新

主手图腾副手为空则则会翻转方块，主手图腾副手不为空且为方块则放出的方块会被反转

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `true`, `false`
- 分类: `PCA`, `feature`

### 刷怪的最大Y值 (spawnYMax)

刷怪的最大 Y 值，会影响刷怪塔效率，114514 为默认

- 类型: `int`
- 默认值: `114514`
- 参考选项: `114514`, `1919810`
- 分类: `PCA`, `feature`

### 刷怪的最小Y值 (spawnYMin)

刷怪的最小 Y 值，会影响刷怪塔效率，114514 为默认

- 类型: `int`
- 默认值: `114514`
- 参考选项: `114514`, `1919810`
- 分类: `PCA`, `feature`

### 全局刷怪群系 (spawnBiome)

全局刷怪群系，会影响整个游戏，DEFAULT 为默认

- 类型: `enum`
- 默认值: `DEFAULT`
- 参考选项: `DESERT`, `PLAINS`
- 分类: `PCA`, `feature`

### 快速叶子腐烂 (quickLeafDecay)

在砍树后树叶会快速掉落

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `true`, `false`
- 分类: `PCA`, `feature`

### Xaero小地图世界名 (xaeroWorldName)

设置 Xaero 世界名来同步世界 ID,"#none" 表示不同步

- 类型: `String`
- 默认值: `#none`
- 参考选项: `#none`
- 分类: `PCA`, `PROTOCOL`

### PCA调试模式 (pcaDebug)

开启后会打印调试信息

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `true`, `false`
- 分类: `PCA`

## License

This template is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.
