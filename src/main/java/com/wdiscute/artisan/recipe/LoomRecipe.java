package com.wdiscute.artisan.recipe;

import com.wdiscute.artisan.ChancedStack;
import com.wdiscute.artisan.registry.ArtisanBlocks;
import com.wdiscute.artisan.registry.ArtisanRecipeSerializers;
import com.wdiscute.artisan.registry.ArtisanRecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.List;

public class LoomRecipe extends AbstractArtisanRecipe
{
    public LoomRecipe(List<Ingredient> ingredients, List<ChancedStack> result, int processing_days, List<Ingredient> requiredUpgrades, int rank)
    {
        super(ArtisanRecipeTypes.LOOM.get(), ingredients, result, processing_days, requiredUpgrades, rank);
    }

    @Override
    public ItemStack getToastSymbol()
    {
        return new ItemStack(ArtisanBlocks.LOOM);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return ArtisanRecipeSerializers.LOOM.get();
    }

}
