package com.wdiscute.artisan.recipe;

import com.wdiscute.artisan.ChancedStack;
import com.wdiscute.artisan.registry.ArtisanBlocks;
import com.wdiscute.artisan.registry.ArtisanRecipeSerializers;
import com.wdiscute.artisan.registry.ArtisanRecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.List;

public class ChargingRodRecipe extends AbstractArtisanRecipe
{
    public ChargingRodRecipe(List<Ingredient> ingredients, List<ChancedStack> result, int processing_days, List<Ingredient> requiredUpgrades, int rank)
    {
        super(ArtisanRecipeTypes.AGING_CASK.get(), ingredients, result, processing_days, requiredUpgrades, rank);
    }

    @Override
    public ItemStack getToastSymbol()
    {
        return new ItemStack(ArtisanBlocks.AGING_CASK);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return ArtisanRecipeSerializers.AGING_CASK.get();
    }

}
