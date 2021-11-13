package com.plusls.carpet;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class PcaMixinPlugin implements IMixinConfigPlugin {

    private boolean hopperoptimizationsLithiumLoaded = false;

    @Override
    public void onLoad(String mixinPackage) {
        hopperoptimizationsLithiumLoaded = FabricLoader.getInstance().isModLoaded("hopperoptimizations-lithium");
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (hopperoptimizationsLithiumLoaded) {
            return !mixinClassName.endsWith("MixinAbstractFurnaceBlockEntity") && !mixinClassName.endsWith("MixinBrewingStandBlockEntity");
        } else {
            return !mixinClassName.endsWith("MixinAbstractFurnaceBlockEntityLithumHopperFix") && !mixinClassName.endsWith("MixinBrewingStandBlockEntityLithumHopperFix");
        }
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
