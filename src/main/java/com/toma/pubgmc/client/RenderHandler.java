package com.toma.pubgmc.client;

import com.toma.pubgmc.animation.HeldAnimation;
import com.toma.pubgmc.api.Game;
import com.toma.pubgmc.client.layers.LayerGhillie;
import com.toma.pubgmc.common.capability.IGameData;
import com.toma.pubgmc.common.capability.IPlayerData;
import com.toma.pubgmc.common.entity.EntityAIPlayer;
import com.toma.pubgmc.common.items.guns.GunBase;
import com.toma.pubgmc.config.ConfigPMC;
import com.toma.pubgmc.event.client.RenderItemInHandEvent;
import com.toma.pubgmc.event.client.SetupAnglesEvent;
import com.toma.pubgmc.init.PMCRegistry;
import com.toma.pubgmc.world.BlueZone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RenderHandler {

    private static List<UUID> playersWithAddedRenderLayer = new ArrayList<>();

    private double interpolate(double current, double previous, double partial) {
        return previous + (current - previous) * partial;
    }

    private void resetModelOffset(ModelBiped modelBiped) {
        this.resetModelRendererOffset(modelBiped.bipedHead);
        this.resetModelRendererOffset(modelBiped.bipedHeadwear);
        this.resetModelRendererOffset(modelBiped.bipedBody);
        this.resetModelRendererOffset(modelBiped.bipedRightArm);
        this.resetModelRendererOffset(modelBiped.bipedLeftArm);
        this.resetModelRendererOffset(modelBiped.bipedRightLeg);
        this.resetModelRendererOffset(modelBiped.bipedLeftLeg);
    }

    private void resetModelRendererOffset(ModelRenderer renderer) {
        renderer.offsetX = 0;
        renderer.offsetY = 0;
        renderer.offsetZ = 0;
    }

    private void setThirdPersonAnimations(ModelBiped model, EntityPlayer player, IPlayerData playerData) {
        if(player.getHeldItemMainhand().getItem() instanceof GunBase) {
            boolean smallWeapon = ((GunBase) player.getHeldItemMainhand().getItem()).getWeaponModel().heldAnimation.getHeldStyle() == HeldAnimation.HeldStyle.SMALL;
            if(smallWeapon) {
                if(player.isSprinting()) {
                    model.bipedRightArm.rotateAngleX = (float)Math.toRadians(-110F);
                } else {
                    boolean aimFlag = playerData.isAiming();
                    model.bipedRightArm.rotateAngleX = (float)Math.toRadians(aimFlag ? -95F : -60F);
                    model.bipedRightArm.rotateAngleY = (float)Math.toRadians(-20F);
                    if(aimFlag) {
                        model.bipedRightArm.rotateAngleZ = (float)Math.toRadians(25F);
                        model.bipedLeftArm.rotateAngleX = (float)Math.toRadians(-90F);
                        model.bipedLeftArm.rotateAngleY = (float)Math.toRadians(40F);
                    }
                }
                return;
            }
            if(player.isSprinting()) {
                model.bipedRightArm.rotateAngleX = (float)Math.toRadians(-50F);
                model.bipedRightArm.rotateAngleY = (float)Math.toRadians(-30F);
                model.bipedRightArm.rotateAngleZ = (float)Math.toRadians(-15F);
                model.bipedLeftArm.rotateAngleX = (float)Math.toRadians(-50F);
                model.bipedLeftArm.rotateAngleY = (float)Math.toRadians(40F);
                return;
            }
            if(playerData.isAiming()) {
                model.bipedRightArm.rotateAngleX = (float)Math.toRadians(-90F);
                model.bipedRightArm.rotateAngleY = (float)Math.toRadians(-15F);
                model.bipedRightArm.rotateAngleZ = (float)Math.toRadians(20F);
                model.bipedLeftArm.rotateAngleX = (float)Math.toRadians(-90F);
                model.bipedLeftArm.rotateAngleY = (float)Math.toRadians(45F);
                return;
            }
            model.bipedRightArm.rotateAngleX = (float)Math.toRadians(-70F);
            model.bipedRightArm.rotateAngleY = (float)Math.toRadians(-30F);
            model.bipedLeftArm.rotateAngleX = model.bipedRightArm.rotateAngleX;
            model.bipedLeftArm.rotateAngleY = (float)Math.toRadians(45F);
        }
    }

    private void setProne(ModelBiped model) {
        model.bipedHead.offsetY = 1.45F;
        model.bipedBody.rotateAngleX = (float)Math.toRadians(90F);
        model.bipedBody.offsetY = 1.35F;
        float rotationAngleX = model.bipedRightArm.rotateAngleX;
        model.bipedRightArm.rotateAngleX = -model.bipedBody.rotateAngleX;
        model.bipedLeftArm.rotateAngleX = model.bipedRightArm.rotateAngleX;
        model.bipedRightArm.rotateAngleY = rotationAngleX * 0.6F + 0.3F;
        model.bipedLeftArm.rotateAngleY = -model.bipedRightArm.rotateAngleY;
        model.bipedRightArm.offsetY = 1.2F;
        model.bipedLeftArm.offsetY = 1.2F;
        model.bipedLeftLeg.rotateAngleX = model.bipedBody.rotateAngleX;
        model.bipedRightLeg.rotateAngleX = model.bipedBody.rotateAngleX;
        model.bipedLeftLeg.offsetY = 0.6F;
        model.bipedRightLeg.offsetY = 0.6F;
        model.bipedRightLeg.offsetZ = 0.7F;
        model.bipedLeftLeg.offsetZ = 0.7F;
        model.bipedLeftLeg.rotateAngleY = model.bipedRightArm.rotateAngleY * 0.4F;
        model.bipedRightLeg.rotateAngleY = -model.bipedRightArm.rotateAngleY * 0.4F;
    }

    @SubscribeEvent
    public void onSetupAngles(SetupAnglesEvent e) {
        Minecraft mc = Minecraft.getMinecraft();
        Entity entity = e.getEntity();
        ModelBiped model = e.getModel();
        if(entity instanceof EntityAIPlayer) {
            if(((EntityAIPlayer) entity).getHeldItemMainhand().getItem() instanceof GunBase) {
                model.bipedRightArm.rotateAngleX = -1.55F;
                model.bipedLeftArm.rotateAngleX = -1.55F;
                model.bipedRightArm.rotateAngleY = -0.35F;
                model.bipedLeftArm.rotateAngleY = 0.8F;
            }
            return;
        }
        if(!(entity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) entity;
        this.resetModelOffset(model);
        ItemStack stack = player.getHeldItemMainhand();
        int perspective = mc.gameSettings.thirdPersonView;
        IPlayerData playerData = IPlayerData.PlayerData.get(player);
        boolean isProne = playerData.isProning();
        // TODO why is it even there?
        switch (perspective) {
            case 0: {
                if(player != mc.getRenderViewEntity()) {
                    if(isProne) {
                        this.setProne(model);
                        break;
                    }
                    this.setThirdPersonAnimations(model, player, playerData);
                }
                break;
            }
            case 1: case 2: {
                if(isProne) {
                    this.setProne(model);
                    break;
                }
                this.setThirdPersonAnimations(model, player, playerData);
                break;
            }
        }
    }

    @SubscribeEvent
    public void onRenderItemInHand(RenderItemInHandEvent e) {
        if(e.entity instanceof EntityPlayer) {
            boolean prone = IPlayerData.PlayerData.get((EntityPlayer) e.entity).isProning();
            if(prone) {
                GlStateManager.translate(0, -1.15F, 0);
            }
        }
    }

    @SubscribeEvent
    public void onFOVChanged(FOVUpdateEvent e) {
        EntityPlayer player = e.getEntity();
        IPlayerData data = player.getCapability(IPlayerData.PlayerDataProvider.PLAYER_DATA, null);
        GameSettings settings = Minecraft.getMinecraft().gameSettings;
        if (data.isAiming()) {
            ItemStack stack = player.getHeldItemMainhand();
            if (stack.getItem() instanceof GunBase) {
                int scope = stack.getItem() == PMCRegistry.PMCItems.VSS ? 4 : stack.hasTagCompound() ? stack.getTagCompound().getInteger("scope") : 0;
                switch (scope) {
                    case 1:
                    case 2:
                        settings.fovSetting = 45;
                        break;
                    case 3:
                        settings.fovSetting = 35;
                        break;
                    case 4:
                        settings.fovSetting = 25;
                        break;
                    case 5:
                        settings.fovSetting = 10;
                        break;
                    case 6:
                        settings.fovSetting = 3;
                        break;
                }
            }
        } else {
            if (settings.fovSetting < 70) settings.fovSetting = 70;
        }
    }

    @SubscribeEvent
    public void renderBlueZone(RenderWorldLastEvent e) {
        Minecraft mc = Minecraft.getMinecraft();
        World world = mc.world;
        IGameData gameData = world.getCapability(IGameData.GameDataProvider.GAMEDATA, null);
        Game game = gameData.getCurrentGame();
        if (!gameData.isPlaying() || gameData.isInactiveGame()) {
            return;
        }
        BlueZone zone = game.zone;
        if (zone == null) {
            return;
        }
        EntityPlayerSP player = mc.player;
        double maxClientRenderDist = mc.gameSettings.renderDistanceChunks * 16;
        if (isCloseToBorder(player, zone, maxClientRenderDist)) {
            double partialTicks = e.getPartialTicks();
            double interpolatedPlayerX = interpolate(player.posX, player.lastTickPosX, partialTicks);
            double interpolatedPlayerY = interpolate(player.posY, player.lastTickPosY, partialTicks);
            double interpolatedPlayerZ = interpolate(player.posZ, player.lastTickPosZ, partialTicks);
            int clientZoneColor = ConfigPMC.client.other.zoneColor;
            float a = 0.25F;
            float r = ((clientZoneColor >> 16) & 255) / 255.0F;
            float g = ((clientZoneColor >> 8) & 255) / 255.0F;
            float b = (clientZoneColor & 255) / 255.0F;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            GlStateManager.disableCull();
            bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
            bufferBuilder.setTranslation(-interpolatedPlayerX, -interpolatedPlayerY, -interpolatedPlayerZ);
            double minRenderPosZ = Math.max(Math.floor(interpolatedPlayerZ - maxClientRenderDist), zone.minZ(partialTicks));
            double maxRenderPosZ = Math.min(Math.ceil(interpolatedPlayerZ + maxClientRenderDist), zone.maxZ(partialTicks));
            double minRenderPosX = Math.max(Math.floor(interpolatedPlayerX - maxClientRenderDist), zone.minX(partialTicks));
            double maxRenderPosX = Math.min(Math.ceil(interpolatedPlayerX + maxClientRenderDist), zone.maxX(partialTicks));
            if (interpolatedPlayerX > zone.maxX(partialTicks) - maxClientRenderDist) {
                bufferBuilder.pos(zone.maxX(partialTicks), 256D, minRenderPosZ).color(r, g, b, a).endVertex();
                bufferBuilder.pos(zone.maxX(partialTicks), 256D, maxRenderPosZ).color(r, g, b, a).endVertex();
                bufferBuilder.pos(zone.maxX(partialTicks), 0D, maxRenderPosZ).color(r, g, b, a).endVertex();
                bufferBuilder.pos(zone.maxX(partialTicks), 0D, minRenderPosZ).color(r, g, b, a).endVertex();
            }
            if (interpolatedPlayerX < zone.minX(partialTicks) + maxClientRenderDist) {
                bufferBuilder.pos(zone.minX(partialTicks), 256D, minRenderPosZ).color(r, g, b, a).endVertex();
                bufferBuilder.pos(zone.minX(partialTicks), 256D, maxRenderPosZ).color(r, g, b, a).endVertex();
                bufferBuilder.pos(zone.minX(partialTicks), 0D, maxRenderPosZ).color(r, g, b, a).endVertex();
                bufferBuilder.pos(zone.minX(partialTicks), 0D, minRenderPosZ).color(r, g, b, a).endVertex();
            }
            if (interpolatedPlayerZ > zone.maxZ(partialTicks) - maxClientRenderDist) {
                bufferBuilder.pos(minRenderPosX, 256D, zone.maxZ(partialTicks)).color(r, g, b, a).endVertex();
                bufferBuilder.pos(maxRenderPosX, 256D, zone.maxZ(partialTicks)).color(r, g, b, a).endVertex();
                bufferBuilder.pos(maxRenderPosX, 0D, zone.maxZ(partialTicks)).color(r, g, b, a).endVertex();
                bufferBuilder.pos(minRenderPosX, 0D, zone.maxZ(partialTicks)).color(r, g, b, a).endVertex();
            }
            if (interpolatedPlayerZ < zone.minZ(partialTicks) + maxClientRenderDist) {
                bufferBuilder.pos(minRenderPosX, 256D, zone.minZ(partialTicks)).color(r, g, b, a).endVertex();
                bufferBuilder.pos(maxRenderPosX, 256D, zone.minZ(partialTicks)).color(r, g, b, a).endVertex();
                bufferBuilder.pos(maxRenderPosX, 0D, zone.minZ(partialTicks)).color(r, g, b, a).endVertex();
                bufferBuilder.pos(minRenderPosX, 0D, zone.minZ(partialTicks)).color(r, g, b, a).endVertex();
            }

            tessellator.draw();
            bufferBuilder.setTranslation(0, 0, 0);
            GlStateManager.enableTexture2D();
            GlStateManager.enableCull();
            GlStateManager.disableBlend();
        }
    }

    @SubscribeEvent
    public void onPlayerRenderPost(RenderPlayerEvent.Post e) {
        EntityPlayer player = e.getEntityPlayer();
        if (playersWithAddedRenderLayer.contains(player.getUniqueID())) {
            return;
        }
        playersWithAddedRenderLayer.add(player.getUniqueID());
        e.getRenderer().addLayer(new LayerGhillie(e.getRenderer()));
    }

    //@SubscribeEvent
    public void renderHand(RenderHandEvent e) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        ItemStack stack = player.getHeldItemMainhand();
        if (!(stack.getItem() instanceof GunBase)) {
            IPlayerData data = player.getCapability(IPlayerData.PlayerDataProvider.PLAYER_DATA, null);
            boolean aim = data.isAiming();
            //boolean oneHand = ((GunBase)stack.getItem()).getWeaponModel().heldAnimation.getHeldStyle() == HeldAnimation.HeldStyle.SMALL;
            RenderManager manager = mc.getRenderManager();
            Render<AbstractClientPlayer> abstractClientPlayerRender = manager.getEntityRenderObject(player);
            RenderPlayer renderPlayer = (RenderPlayer) abstractClientPlayerRender;
            GlStateManager.pushMatrix();
            GlStateManager.disableCull();

            GlStateManager.rotate(90F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(92.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(41.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(-0.3F, -1.1F, 0.45F);
            renderPlayer.renderLeftArm(mc.player);
            GlStateManager.enableCull();
            GlStateManager.popMatrix();
        }
    }

    public void renderHand() {

    }

    public float prepareScale(EntityPlayer player, float partial) {
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        GlStateManager.scale(1.9F, 1.9F, 1.9F);
        GlStateManager.translate(0.0F, -1.5F, 0.0F);
        return 0.0625F;
    }

    public float normalizeAndInterpolateRotation(float prev, float current, float partial) {
        float f = current - prev;
        while (f < -180F) f += 360F;
        while (f >= 180.0F) f -= 360.0F;
        return prev + partial * f;
    }

    public boolean isCloseToBorder(EntityPlayerSP player, BlueZone zone, double maxDist) {
        return player.posX >= zone.maxX(1.0F) - maxDist || player.posX <= zone.minX(1.0F) + maxDist || player.posZ >= zone.maxZ(1.0F) - maxDist || player.posZ <= zone.minZ(1.0F) + maxDist;
    }
}
