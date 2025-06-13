package com.aluminium.flowchart.gui;

import com.aluminium.flowchart.Flowchart;
import com.aluminium.flowchart.Tags;
import com.aluminium.flowchart.utils.Node;
import com.cleanroommc.modularui.GuiError;
import com.cleanroommc.modularui.api.GuiAxis;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.factory.ClientGUI;
import com.cleanroommc.modularui.integration.jei.JeiRecipeTransferHandler;
import com.cleanroommc.modularui.screen.*;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.value.sync.FluidSlotSyncHandler;
import com.cleanroommc.modularui.widget.DraggableWidget;
import com.cleanroommc.modularui.widget.ScrollWidget;
import com.cleanroommc.modularui.widget.WidgetTree;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;
import com.cleanroommc.modularui.widgets.slot.PhantomItemSlot;
import com.github.vfyjxf.jeiutilities.jei.ingredient.RecipeInfo;
import com.google.common.collect.ImmutableList;
import mezz.jei.Internal;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.bookmarks.BookmarkList;
import mezz.jei.ingredients.Ingredients;
import mezz.jei.recipes.RecipeMap;
import mezz.jei.recipes.RecipeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.items.ItemStackHandler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//@SideOnly(Side.CLIENT)
public class Gui extends CustomModularScreen {

    private int recipes = 0;
//    private static int ITEMSIZE = 16;
    private static ItemStack[] outputPreview;
    private static FluidStack[] outputPreview2;
    private static Ingredients[] ingredients;
    private static ArrayList<Node> nodes;
    private static Node selection;

    public static void open() {
        UISettings settings = new UISettings();
        settings.getJeiSettings().enableJei();
        settings.customContainer(ModularContainer::new);

        ClientGUI.open(new Gui(), settings);
    }

    @Override
    public ModularPanel buildUI(ModularGuiContext context) {
        if (ingredients == null) {
            ingredients = new Ingredients[256];
            Arrays.fill(ingredients, new Ingredients());
        }
        if (outputPreview == null) {
            outputPreview = new ItemStack[ingredients.length];
        }
        if (outputPreview2 == null) {
            outputPreview2 = new FluidStack[ingredients.length];
        }
        if (nodes == null) {
            nodes = new ArrayList<>();
        }


        ScrollWidget<?> node = new ScrollWidget<>()
                .size(600, 448)
                .pos(32, 24)
                .hoverBackground(GuiTextures.SLOT_ITEM)
                .keepScrollBarInArea()
                .background(UITexture.builder()
                        .location(Tags.MOD_ID, "background")
                        .imageSize(24, 24)
                        .tiled()
                        .uv(0, 0, 24, 24)
                        .name("tiled_graph_background")
                        .build());

        renderNodes(node);

        ScrollWidget<?> w1 =  new ScrollWidget<>(new VerticalScrollData())
                .size(24, 448)
                .pos(8, 24)
                .hoverBackground(GuiTextures.SLOT_ITEM)
                .child(new ButtonWidget<>()
                        .onMousePressed(mouseButton -> {
                            if (mouseButton == 0 | mouseButton == 1) {
                                getBookmarks();
                                Interactable.playButtonClickSound();
                                open();
                            }
                            return false;
                        }))
                .keepScrollBarInArea();
        w1.getScrollArea().getScrollY().setScrollSize(18*258);

        for (int i = 0; i < ingredients.length; i++) {
            Ingredients ingredient = ingredients[i];

            if (outputPreview[i] != null) {
                ItemStackHandler handler = new ItemStackHandler();
                handler.setStackInSlot(0, outputPreview[i]);

                w1.child(new PhantomItemSlot() {
                    @Override
                    public Result onMousePressed(int mouseButton) {
                        Interactable.playButtonClickSound();
                        nodes.add(uningredient(ingredient));
                        node.getChildren().clear();
                        renderNodes(node);
                        WidgetTree.resize(node);
                        return Result.SUCCESS;
                    }

                    @Override
                    public boolean onMouseScroll(UpOrDown scrollDirection, int amount) {
                        return true;
                    }
                }
                        .slot(handler, 0)
                        .pos(0, 18 + 18 * i));
            }
            else if (outputPreview2[i] != null) {
                FluidSlotSyncHandler handler = new FluidSlotSyncHandler(new FluidTank(outputPreview2[i], 1));
                handler.phantom(true);
                handler.updateCacheFromSource(true);

                w1.child(new FluidSlot() {
                    @Override
                    public Result onMousePressed(int mouseButton) {
                        Interactable.playButtonClickSound();
                        nodes.add(uningredient(ingredient));
                        node.getChildren().clear();
                        renderNodes(node);
                        WidgetTree.resize(node);
                        return Result.SUCCESS;
                    }

                    @Override
                    public boolean onMouseScroll(UpOrDown scrollDirection, int amount) {
                        return true;
                    }
                }
                        .syncHandler(handler)
                        .pos(0, 18 + 18 * i));
            }
        }

//        Flowchart.LOGGER.info(ReflectionToStringBuilder.toString(ingredients));

        ModularPanel panel = ModularPanel.defaultPanel("flowchart_panel_2", 640, 480);
        panel.child(IKey.str("Flowchart, a: " + recipes).asWidget()
                        .top(7).left(7))
                .child(w1)
                .child(node);

        return panel;
    }

    private void renderNodes(ScrollWidget<?> nodeWidget) {
        for (Node node : nodes) {
            NodeWidget<?> widget = new NodeWidget<>(new VerticalScrollData(), () -> selection = node, node)
                    .pos(node.x, node.y)
                    .size(24, 24)
                    .background(GuiTextures.DISPLAY_SMALL)
                    .keepScrollBarInArea();

            nodeWidget.child(widget);
        }
    }

    private Node uningredient(Ingredients ingredient) {
        List<ItemStack> inputItem = new ArrayList<>();
        List<ItemStack> outputItem = new ArrayList<>();
        List<FluidStack> inputFluid = new ArrayList<>();
        List<FluidStack> outputFluid = new ArrayList<>();

        for (List<ItemStack> itemList : ingredient.getInputs(VanillaTypes.ITEM)) {
            inputItem.addAll(itemList);
        }

        for (List<FluidStack> itemList : ingredient.getInputs(VanillaTypes.FLUID)) {
            inputFluid.addAll(itemList);
        }

        for (List<ItemStack> itemList : ingredient.getOutputs(VanillaTypes.ITEM)) {
            outputItem.addAll(itemList);
        }

        for (List<FluidStack> itemList : ingredient.getOutputs(VanillaTypes.FLUID)) {
            outputFluid.addAll(itemList);
        }

        return new Node(inputItem.toArray(new ItemStack[0]), inputFluid.toArray(new FluidStack[0]), outputItem.toArray(new ItemStack[0]), outputFluid.toArray(new FluidStack[0]));
    }

    private void getBookmarks() {
        BookmarkList bookmark = Internal.getBookmarkList();
        int i = 0;
        int j = 0;
        try {
            Field field = BookmarkList.class.getDeclaredField("list");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<Object> recipes = (List<Object>) field.get(bookmark);
            for (Object recipe_ : recipes) {
                if (recipe_ instanceof RecipeInfo) {
                    RecipeInfo<?, ?> recipe = (RecipeInfo<?, ?>) recipe_;
//                    recipe.getIngredient();
                    Object ingredient = recipe.getIngredient();
                    Object result = recipe.getResult();

//                    Flowchart.LOGGER.info(recipe.toString());

                    if (Internal.getRuntime() != null) {
                        RecipeRegistry registry = Internal.getRuntime().getRecipeRegistry();
                        IRecipeCategory<?> category = registry.getRecipeCategory(recipe.getRecipeCategoryUid());
                        Field output = RecipeRegistry.class.getDeclaredField("recipeOutputMap");
                        output.setAccessible(true);
                        RecipeMap outputMap = (RecipeMap) output.get(registry);
                        if (category != null) {
                            @SuppressWarnings("unchecked")
                            ImmutableList<IRecipeWrapper> recipes2 = (ImmutableList<IRecipeWrapper>) outputMap.getRecipeWrappers(category, ingredient);


                            IRecipeWrapper recipeWrapper = recipes2.get(recipe.getRecipeIndex());
                            Ingredients ingredients = new Ingredients();
                            recipeWrapper.getIngredients(ingredients);

                            Gui.ingredients[j] = ingredients;
                            if (result instanceof ItemStack) {
                                outputPreview[j] = (ItemStack) result;
                                outputPreview2[j] = null;
                            }
                            else {
                                outputPreview[j] = null;
                                outputPreview2[j] = (FluidStack) result;
                            }
                            j += 1;
                        }
                    }

//                    if (ingredient instanceof ItemStack) {
//                        items[i] = (ItemStack) ingredient;
//                        i += 1;
//                    }
//                    if (result instanceof ItemStack) {
//                        items[i] = (ItemStack) result;
//                        i += 1;
//                    }
                }
            }
        } catch (Exception e) {
            Flowchart.LOGGER.error(e, e);
        }
    }
}
