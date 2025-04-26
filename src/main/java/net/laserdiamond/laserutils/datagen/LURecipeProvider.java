package net.laserdiamond.laserutils.datagen;

import net.laserdiamond.laserutils.item.equipment.SmithingTemplateCraftableItem;
import net.laserdiamond.laserutils.item.equipment.SmithingTransformItem;
import net.laserdiamond.laserutils.item.equipment.armor.GenericArmorCraftableItem;
import net.laserdiamond.laserutils.item.equipment.tools.GenericToolCraftableItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Recipe provider
 * @param <T> The {@link LUDataGenerator} class
 */
public class LURecipeProvider<T extends LUDataGenerator<T>> extends RecipeProvider implements IConditionBuilder {

    protected final T dataGenerator;

    /**
     * Creates a new {@link LURecipeProvider}
     * @param packOutput The {@link PackOutput} of the {@link net.minecraft.data.DataGenerator}
     * @param dataGenerator The {@link LUDataGenerator}, specified by {@link T}
     */
    public LURecipeProvider(PackOutput packOutput, T dataGenerator) {
        super(packOutput);
        this.dataGenerator = dataGenerator;
    }

    @Override
    protected final void buildRecipes(Consumer<FinishedRecipe> recipeOutput)
    {
        this.dataGenerator.itemDeferredRegister().getEntries().forEach((itemRegistryObject) ->
        {
            this.craftAdditionalItems(itemRegistryObject, recipeOutput);
            this.craftArmorItems(itemRegistryObject, recipeOutput);
            this.craftToolItems(itemRegistryObject, recipeOutput);
            this.craftSmithingItems(itemRegistryObject, recipeOutput);
            this.createSmithingTemplateRecipes(itemRegistryObject, recipeOutput);
        });
    }

    /**
     * Creates the recipes for all {@link ArmorItem}s
     * @param itemRegistryObject The {@link Item} {@link RegistryObject} to create the recipe for
     * @param recipeOutput The {@linkplain FinishedRecipe Finished Recipe Consumer} of the {@link RecipeProvider}
     * @see GenericArmorCraftableItem
     */
    protected final void craftArmorItems(RegistryObject<Item> itemRegistryObject, Consumer<FinishedRecipe> recipeOutput)
    {
        Item item = itemRegistryObject.get();

        if (item instanceof ArmorItem armorItem)
        {
            EquipmentSlot equipmentSlot = armorItem.getEquipmentSlot();
            if (armorItem instanceof GenericArmorCraftableItem armorCraftableItem)
            {
                ItemLike materialItem = armorCraftableItem.materialItem();

                this.craftAdditionalArmorItems(itemRegistryObject, armorCraftableItem, recipeOutput);

                switch (equipmentSlot)
                {
                    case HEAD -> ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, armorItem)
                            .pattern("XXX")
                            .pattern("X X")
                            .define('X', materialItem)
                            .unlockedBy(getHasName(materialItem), has(materialItem))
                            .save(recipeOutput);
                    case CHEST -> ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, armorItem)
                            .pattern("X X")
                            .pattern("XXX")
                            .pattern("XXX")
                            .define('X', materialItem)
                            .unlockedBy(getHasName(materialItem), has(materialItem))
                            .save(recipeOutput);
                    case LEGS -> ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, armorItem)
                            .pattern("XXX")
                            .pattern("X X")
                            .pattern("X X")
                            .define('X', materialItem)
                            .unlockedBy(getHasName(materialItem), has(materialItem))
                            .save(recipeOutput);
                    case FEET -> ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, armorItem)
                            .pattern("X X")
                            .pattern("X X")
                            .define('X', materialItem)
                            .unlockedBy(getHasName(materialItem), has(materialItem))
                            .save(recipeOutput);
                }
            }
        }
    }

    /**
     * Creates any other additional armor recipes
     * @param itemRegistryObject The {@link Item} {@link RegistryObject} to create the recipe for
     * @param armorCraftableItem The {@link GenericArmorCraftableItem} instance type of the {@link Item}
     * @param recipeOutput The {@linkplain FinishedRecipe Finished Recipe Consumer} of the {@link RecipeProvider}
     * @see GenericArmorCraftableItem
     */
    protected void craftAdditionalArmorItems(RegistryObject<Item> itemRegistryObject, GenericArmorCraftableItem armorCraftableItem, Consumer<FinishedRecipe> recipeOutput) {}

    /**
     * Creates all the recipes for all tool items
     * @param itemRegistryObject The {@link Item} {@link RegistryObject} to create the recipe for
     * @param recipeOutput The {@linkplain FinishedRecipe Finished Recipe Consumer} of the {@link RecipeProvider}
     * @see GenericToolCraftableItem
     */
    protected final void craftToolItems(RegistryObject<Item> itemRegistryObject, Consumer<FinishedRecipe> recipeOutput)
    {
        Item item = itemRegistryObject.get();

        if (item instanceof GenericToolCraftableItem craftableItem)
        {
            ItemLike materialItem = craftableItem.materialItem();
            ItemLike stickItem = craftableItem.stickItem();

            this.craftAdditionalToolItems(itemRegistryObject, craftableItem, recipeOutput);

            if (item instanceof SwordItem swordItem)
            {
                ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, swordItem)
                        .pattern("X")
                        .pattern("X")
                        .pattern("S")
                        .define('X', materialItem)
                        .define('S', stickItem)
                        .unlockedBy(getHasName(materialItem), has(materialItem))
                        .save(recipeOutput);
            } else if (item instanceof PickaxeItem pickaxeItem)
            {
                ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, pickaxeItem)
                        .pattern("XXX")
                        .pattern(" S")
                        .pattern(" S")
                        .define('X', materialItem)
                        .define('S', stickItem)
                        .unlockedBy(getHasName(materialItem), has(materialItem))
                        .save(recipeOutput);
            } else if (item instanceof AxeItem axeItem)
            {
                ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, axeItem)
                        .pattern("XX")
                        .pattern("XS")
                        .pattern(" S")
                        .define('X', materialItem)
                        .define('S', stickItem)
                        .unlockedBy(getHasName(materialItem), has(materialItem))
                        .save(recipeOutput);
            } else if (item instanceof ShovelItem shovelItem)
            {
                ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, shovelItem)
                        .pattern("X")
                        .pattern("S")
                        .pattern("S")
                        .define('X', materialItem)
                        .define('S', stickItem)
                        .unlockedBy(getHasName(materialItem), has(materialItem))
                        .save(recipeOutput);
            } else if (item instanceof HoeItem hoeItem)
            {
                ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, hoeItem)
                        .pattern("XX")
                        .pattern(" S")
                        .pattern(" S")
                        .define('X', materialItem)
                        .define('S', stickItem)
                        .unlockedBy(getHasName(materialItem), has(materialItem))
                        .save(recipeOutput);
            }
        }
    }

    /**
     * Creates any other additional tool recipes
     * @param itemRegistryObject The {@link Item} {@link RegistryObject} to create a recipe for
     * @param toolCraftableItem The {@link GenericToolCraftableItem} instance type of the {@link Item}
     * @param recipeOutput The {@linkplain FinishedRecipe Finished Recipe Consumer} of the {@link RecipeProvider}
     * @see GenericToolCraftableItem
     */
    protected void craftAdditionalToolItems(RegistryObject<Item> itemRegistryObject, GenericToolCraftableItem toolCraftableItem, Consumer<FinishedRecipe> recipeOutput) {}

    /**
     * Creates all smithing table-related recipes
     * @param itemRegistryObject The {@link Item} {@link RegistryObject} to create the recipe for
     * @param recipeOutput The {@linkplain FinishedRecipe Finished Recipe Consumer} of the {@link RecipeProvider}
     */
    protected final void craftSmithingItems(RegistryObject<Item> itemRegistryObject, Consumer<FinishedRecipe> recipeOutput)
    {
        Item item = itemRegistryObject.get();

        if (item instanceof SmithingTransformItem smithingTransformItem)
        {
            ItemLike materialItem = smithingTransformItem.materialItem();
            ItemLike templateItem = smithingTransformItem.templateItem();
            ItemLike equipmentItem = smithingTransformItem.equipmentPiece();

            SmithingTransformRecipeBuilder
                    .smithing(Ingredient.of(templateItem), Ingredient.of(equipmentItem), Ingredient.of(materialItem), RecipeCategory.COMBAT, item)
                    .unlocks(getHasName(smithingTransformItem.materialItem()), has(smithingTransformItem.materialItem()))
                    .save(recipeOutput, getItemName(item) + "_smithing");

            this.craftAdditionalSmithingItems(itemRegistryObject, smithingTransformItem, recipeOutput);
        }
    }

    /**
     * Creates any other additional smithing table recipes
     * @param itemRegistryObject The {@link Item} {@link RegistryObject} to create a recipe for
     * @param smithingTransformItem The {@link SmithingTransformItem} instance type of the {@link Item}
     * @param recipeOutput The {@linkplain FinishedRecipe Finished Recipe Consumer} of the {@link RecipeProvider}
     */
    protected void craftAdditionalSmithingItems(RegistryObject<Item> itemRegistryObject, SmithingTransformItem smithingTransformItem, Consumer<FinishedRecipe> recipeOutput) {}

    /**
     * Creates all the smithing template recipes
     * @param itemRegistryObject The {@link Item} {@link RegistryObject} to create the recipe for
     * @param recipeOutput The {@linkplain FinishedRecipe Finished Recipe Consumer} of the {@link RecipeProvider}
     */
    protected void createSmithingTemplateRecipes(RegistryObject<Item> itemRegistryObject, Consumer<FinishedRecipe> recipeOutput)
    {
        Item item = itemRegistryObject.get();

        if (item instanceof SmithingTemplateCraftableItem smithingTemplateCraftableItem)
        {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item, 2)
                    .pattern("XTX")
                    .pattern("XBX")
                    .pattern("XXX")
                    .define('X', smithingTemplateCraftableItem.gemItem())
                    .define('B', smithingTemplateCraftableItem.baseItem())
                    .define('T', item)
                    .unlockedBy(getHasName(item), has(item))
                    .save(recipeOutput);

            createAdditionalSmithingTemplateRecipes(itemRegistryObject, smithingTemplateCraftableItem, recipeOutput);
        }
    }

    /**
     * Creates any additional smithing template recipes
     * @param itemRegistryObject The {@link Item} {@link RegistryObject} to create a recipe for
     * @param smithingTemplateCraftableItem The {@link SmithingTemplateCraftableItem} instance type of the item
     * @param recipeOutput The {@linkplain FinishedRecipe Finished Recipe Consumer} of the {@link RecipeProvider}
     */
    protected void createAdditionalSmithingTemplateRecipes(RegistryObject<Item> itemRegistryObject, SmithingTemplateCraftableItem smithingTemplateCraftableItem, Consumer<FinishedRecipe> recipeOutput) {}

    /**
     * Creates any other additional recipes for items
     * @param itemRegistryObject The {@link Item} {@link RegistryObject} to create a recipe for
     * @param recipeOutput The {@linkplain FinishedRecipe Finished Recipe Consumer} of the {@link RecipeProvider}
     */
    protected void craftAdditionalItems(RegistryObject<Item> itemRegistryObject, Consumer<FinishedRecipe> recipeOutput) {}


}
