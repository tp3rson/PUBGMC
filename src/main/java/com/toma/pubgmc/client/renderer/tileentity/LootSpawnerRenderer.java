package com.toma.pubgmc.client.renderer.tileentity;

import com.toma.pubgmc.common.items.guns.GunBase;
import com.toma.pubgmc.common.items.heal.ItemBandage;
import com.toma.pubgmc.common.items.heal.ItemEnergyDrink;
import com.toma.pubgmc.common.items.heal.ItemFirstAidKit;
import com.toma.pubgmc.common.items.heal.ItemPainkiller;
import com.toma.pubgmc.common.tileentity.TileEntityLootGenerator;
import com.toma.pubgmc.config.ConfigPMC;
import com.toma.pubgmc.config.client.CFGLootRenderStyle;
import com.toma.pubgmc.init.PMCRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class LootSpawnerRenderer extends TileEntitySpecialRenderer<TileEntityLootGenerator> {
    private EntityItem entityItem = new EntityItem(null, 0D, 0D, 0D);
    private RenderEntityItem itemRenderer;

    @Override
    public void render(TileEntityLootGenerator te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (te == null || te.isInvalid()) {
            return;
        }

        //small correction
        y = y - 0.1;
        World world = this.getWorld();
        renderItem(te, x + 0.4, y, z - 0.25, 0, partialTicks);
        renderItem(te, x - 0.2, y, z - 0.3, 1, partialTicks);
        renderItem(te, x + 0.3, y, z + 0.1, 2, partialTicks);
        renderItem(te, x, y, z - 0.35, 3, partialTicks);
        renderItem(te, x + 0.3, y, z - 0.6, 4, partialTicks);
        renderItem(te, x - 0.3, y, z - 0.6, 5, partialTicks);
        renderItem(te, x - 0.3, y, z + 0.1, 6, partialTicks);
        renderItem(te, x, y, z - 0.6, 7, partialTicks);
        renderItem(te, x, y, z, 8, partialTicks);
    }

    private void renderItem(TileEntityLootGenerator te, double x, double y, double z, int slot, float ticks) {
        boolean is3D = te.getStackInSlot(slot).getItem() instanceof GunBase;
        boolean drinkable = te.getStackInSlot(slot).getItem() instanceof ItemEnergyDrink || te.getStackInSlot(slot).getItem() instanceof ItemPainkiller;
        boolean firstAid = te.getStackInSlot(slot).getItem() instanceof ItemFirstAidKit || te.getStackInSlot(slot).getItem() instanceof ItemBandage;
        boolean medkit = te.getStackInSlot(slot).getItem() == PMCRegistry.PMCItems.MEDKIT;

        if (ConfigPMC.client.other.lootRenderStyle == CFGLootRenderStyle.FANCY) {
            if (te.isInvalid() || te == null) {
                return;
            }

            if (te.getStackInSlot(slot).isEmpty()) {
                return;
            }

            entityItem.setItem(te.getStackInSlot(slot));

            if (te.getStackInSlot(slot).getItem() == PMCRegistry.PMCItems.FUELCAN) {
                GlStateManager.pushMatrix();
                GL11.glPushMatrix();
                GL11.glDisable(GL11.GL_LIGHTING);

                this.entityItem.hoverStart = 0.0F;
                GlStateManager.translate((float) x + 0.5F, (float) y + 0.05F, (float) z + 0.5F);
                GlStateManager.translate(0, 0.1, 0);
                GlStateManager.scale(0.9F, 0.9F, 0.9F);

                Minecraft.getMinecraft().getRenderManager().renderEntity(entityItem, 0.0D, 0.0D, 0.1D, 0.0F, 0.0F, false);

                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glPopMatrix();
                GlStateManager.popMatrix();
            } else if (is3D) {
                weaponRenderer(slot, te, x, y, z);
            } else if (drinkable) {
                boosterRenderer(slot, te, x, y, z);
            } else if (firstAid) {
                firstAidKitRenderer(slot, te, x, y, z);
            } else if (medkit) {
                medkitRenderer(slot, te, x, y, z);
            } else {
                GL11.glPushMatrix();
                GL11.glDisable(GL11.GL_LIGHTING);

                this.entityItem.hoverStart = 0.0F;
                GlStateManager.translate((float) x + 0.5F, (float) y + 0.05F, (float) z + 0.5F);
                GlStateManager.rotate(180, 0, 1, 1);
                GlStateManager.scale(0.9F, 0.9F, 0.9F);

                Minecraft.getMinecraft().getRenderManager().renderEntity(entityItem, 0.0D, 0.0D, 0.1D, 0.0F, 0.0F, false);

                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glPopMatrix();
            }
        } else if (ConfigPMC.client.other.lootRenderStyle == CFGLootRenderStyle.FAST) {
            if (te.isInvalid() || te == null || te.getStackInSlot(slot).isEmpty()) {
                return;
            }

            if (itemRenderer == null) {
                itemRenderer = new RenderEntityItem(Minecraft.getMinecraft().getRenderManager(), Minecraft.getMinecraft().getRenderItem()) {
                    @Override
                    protected int getModelCount(ItemStack stack) {
                        return 1;
                    }

                    @Override
                    public boolean shouldBob() {
                        return false;
                    }

                    @Override
                    public boolean shouldSpreadItems() {
                        return false;
                    }
                };
            }

            GlStateManager.pushMatrix();
            {
                entityItem.setItem(te.getStackInSlot(slot));
                entityItem.hoverStart = 0f;
                GlStateManager.translate(0.5, 0, 0.75);

                if (firstAid) {
                    GlStateManager.translate(0, 0.3, 0);
                } else if (drinkable || medkit) {
                    GlStateManager.translate(0, 0.2, 0);
                }

                itemRenderer.doRender(entityItem, x, y, z, 0f, ticks);
            }
            GlStateManager.popMatrix();
        }
    }

    private void medkitRenderer(int slot, TileEntityLootGenerator te, double x, double y, double z) {
        GlStateManager.pushMatrix();

        GlStateManager.disableLighting();

        this.entityItem.hoverStart = 0.0F;
        GlStateManager.translate((float) x + 0.1F, (float) y + 0.1F, (float) z + 0.7F);
        GlStateManager.rotate(90, 0, 1, 0);
        GlStateManager.rotate(45, 0, 1, 0);
        GlStateManager.scale(0.6F, 0.6F, 0.6F);

        Minecraft.getMinecraft().getRenderManager().renderEntity(entityItem, 0.0D, 0.0D, 0.1D, 0.0F, 0.0F, false);

        GlStateManager.enableLighting();

        GlStateManager.popMatrix();
    }

    private void boosterRenderer(int slot, TileEntityLootGenerator te, double x, double y, double z) {
        GlStateManager.pushMatrix();

        GlStateManager.disableLighting();

        this.entityItem.hoverStart = 0.0F;
        GlStateManager.translate((float) x + 0.1F, (float) y + 0.1F, (float) z + 0.7F);
        GlStateManager.rotate(90, 0, 1, 0);
        GlStateManager.scale(0.9F, 0.9F, 0.9F);

        if (te.getStackInSlot(slot).getItem() == PMCRegistry.PMCItems.PAINKILLERS) {
            GlStateManager.translate(0f, 0.11f, 0f);
            GlStateManager.scale(1f, 1.25f, 1f);
        }

        Minecraft.getMinecraft().getRenderManager().renderEntity(entityItem, 0.0D, 0.0D, 0.1D, 0.0F, 0.0F, false);

        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private void firstAidKitRenderer(int slot, TileEntityLootGenerator te, double x, double y, double z) {
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();

        this.entityItem.hoverStart = 0f;

        GlStateManager.translate(x + 0.5f, y + 0.1f, z + 0.5f);
        GlStateManager.rotate(90, 0, 1, 0);
        GlStateManager.scale(0.8f, 0.8f, 0.8f);

        Minecraft.getMinecraft().getRenderManager().renderEntity(entityItem, 0d, 0d, 0.1d, 0f, 0f, false);

        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private void weaponRenderer(int slot, TileEntityLootGenerator te, double x, double y, double z) {
        GlStateManager.pushMatrix();

        GL11.glPushMatrix();
        //Use GL11 lighting since Minecraft makes it quite dark
        GL11.glDisable(GL11.GL_LIGHTING);

        this.entityItem.hoverStart = 0.0F;
        GlStateManager.translate(x + 0.3f, y + 0.18f, z + 0.8f);
        GlStateManager.rotate(180, 1f, 1f, 0f);
        GlStateManager.scale(0.6f, 0.6f, 0.6f);

        Minecraft.getMinecraft().getRenderManager().renderEntity(entityItem, 0.0D, 0.0D, 0.1D, 0.0F, 0.0F, false);

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();

        GlStateManager.popMatrix();
    }
}
