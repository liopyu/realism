package net.liopyu.realism.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import net.liopyu.realism.Realism;
import net.liopyu.realism.util.RegistryUtils;

public class RealismLangProvider extends LanguageProvider {

    public RealismLangProvider(DataGenerator gen, String locale) {
        super(gen, Realism.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        RegistryUtils.BLOCKS.forEach((name, lazyBlock) -> addBlock(lazyBlock, capitalize(name.replace("_", " "))));
    }

    private String capitalize(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
