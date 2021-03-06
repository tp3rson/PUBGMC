package com.toma.pubgmc.common.capability;

import com.toma.pubgmc.Pubgmc;
import com.toma.pubgmc.network.PacketHandler;
import com.toma.pubgmc.network.sp.PacketClientCapabilitySync;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public interface IPlayerData extends INBTSerializable<NBTTagCompound> {
    boolean isReloading();

    void setReloading(boolean reloading);

    boolean isAiming();

    void setAiming(boolean aiming);

    void setNV(boolean nv);

    boolean isUsingNV();

    int getReloadingTime();

    void setReloadingTime(int rt);

    int getTimer();

    void setTimer(int t);

    void addBoost(float boost);

    void removeBoost(float boost);

    float getBoost();

    void setBoost(float boost);

    void setBackpackLevel(int level);

    int getBackpackLevel();

    void hasEquippedNV(boolean nv);

    boolean getEquippedNV();

    boolean isProning();

    // proning
    void setProning(boolean proning);

    int getScopeType();

    //scope variants
    void setScopeType(int type);

    int getScopeColor();

    void setScopeColor(int color);

    double getDistance();

    // map drop location distance
    void setDistance(double dist);

    void sync(EntityPlayer player);

    NBTTagCompound serializePacketNBT();

    void deserializePacketNBT(NBTTagCompound nbt);

    class PlayerDataStorage implements IStorage<IPlayerData> {
        @Override
        public NBTBase writeNBT(Capability<IPlayerData> capability, IPlayerData instance, EnumFacing side) {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<IPlayerData> capability, IPlayerData instance, EnumFacing side, NBTBase nbt) {
            instance.deserializeNBT(nbt instanceof NBTTagCompound ? (NBTTagCompound)nbt : new NBTTagCompound());
        }
    }

    class PlayerData implements IPlayerData {
        private boolean reloading;
        private boolean aiming;
        private boolean nv;
        private int reloading_time;
        private int timer;
        private float boost;

        private boolean isProne;

        private int level;
        private boolean eqNV;

        private int scopetype;
        private int scopecolor;

        private double dist;

        public PlayerData() {}

        public static IPlayerData get(EntityPlayer player) {
            if (player.hasCapability(PlayerDataProvider.PLAYER_DATA, null)) {
                return player.getCapability(PlayerDataProvider.PLAYER_DATA, null);
            }

            return null;
        }

        @Override
        public void setBackpackLevel(int level) {
            this.level = level;
        }

        @Override
        public int getBackpackLevel() {
            return this.level;
        }

        @Override
        public void hasEquippedNV(boolean nv) {
            this.eqNV = nv;
        }

        @Override
        public boolean getEquippedNV() {
            return this.eqNV;
        }

        @Override
        public boolean isReloading() {
            return this.reloading;
        }

        @Override
        public void setReloading(boolean reloading) {
            this.reloading = reloading;
        }

        @Override
        public boolean isAiming() {
            return this.aiming;
        }

        @Override
        public void setAiming(boolean aiming) {
            this.aiming = aiming;
        }

        @Override
        public boolean isUsingNV() {
            return this.nv;
        }

        @Override
        public int getReloadingTime() {
            return this.reloading_time;
        }

        @Override
        public void setReloadingTime(int rt) {
            this.reloading_time = rt;
        }

        @Override
        public int getTimer() {
            return this.timer;
        }

        @Override
        public void setTimer(int t) {
            this.timer = t;
        }

        @Override
        public float getBoost() {
            return this.boost;
        }

        @Override
        public void setBoost(float boost) {
            this.boost = boost;
        }

        @Override
        public void setNV(boolean nv) {
            this.nv = nv;
        }

        @Override
        public void addBoost(float boost) {
            this.boost += boost;
        }

        @Override
        public void removeBoost(float boost) {
            this.boost -= boost;
        }

        @Override
        public int getScopeType() {
            return scopetype;
        }

        @Override
        public void setScopeType(int type) {
            this.scopetype = type;
        }

        @Override
        public int getScopeColor() {
            return scopecolor;
        }

        @Override
        public void setScopeColor(int color) {
            this.scopecolor = color;
        }

        @Override
        public double getDistance() {
            return dist;
        }

        @Override
        public void setDistance(double dist) {
            this.dist = dist;
        }

        @Override
        public boolean isProning() {
            return isProne;
        }

        @Override
        public void setProning(boolean proning) {
            this.isProne = proning;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound c = new NBTTagCompound();
            c.setFloat("boost", boost);
            c.setInteger("level", level);
            c.setBoolean("eqnv", eqNV);
            c.setInteger("scopetype", scopetype);
            c.setInteger("scopecolor", scopecolor);
            return c;
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            boost = nbt.getFloat("boost");
            level = nbt.getInteger("level");
            eqNV = nbt.getBoolean("eqnv");
            scopetype = nbt.getInteger("scopetype");
            scopecolor = nbt.getInteger("scopecolor");
        }

        @Override
        public NBTTagCompound serializePacketNBT() {
            NBTTagCompound nbt = this.serializeNBT();
            nbt.setBoolean("aim", this.aiming);
            nbt.setBoolean("reload", this.reloading);
            nbt.setBoolean("prone", this.isProne);
            return nbt;
        }

        @Override
        public void deserializePacketNBT(NBTTagCompound nbt) {
            this.deserializeNBT(nbt);
            this.aiming = nbt.getBoolean("aim");
            this.reloading = nbt.getBoolean("reload");
            this.isProne = nbt.getBoolean("prone");
        }

        @Override
        public void sync(EntityPlayer player) {
            PacketHandler.sendToAllClients(new PacketClientCapabilitySync(player, this.serializePacketNBT()));
        }
    }

    class PlayerDataProvider implements ICapabilitySerializable<NBTBase> {
        @CapabilityInject(IPlayerData.class)
        public static final Capability<IPlayerData> PLAYER_DATA = null;

        private IPlayerData instance = PLAYER_DATA.getDefaultInstance();

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
            return capability == PLAYER_DATA;
        }

        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
            return capability == PLAYER_DATA ? PLAYER_DATA.cast(this.instance) : null;
        }

        @Override
        public NBTBase serializeNBT() {
            return PLAYER_DATA.getStorage().writeNBT(PLAYER_DATA, this.instance, null);
        }

        @Override
        public void deserializeNBT(NBTBase nbt) {
            PLAYER_DATA.getStorage().readNBT(PLAYER_DATA, this.instance, null, nbt);
        }
    }

    @Mod.EventBusSubscriber
    class Events {

        @SubscribeEvent
        public static void attach(AttachCapabilitiesEvent<Entity> e) {
            if(e.getObject() instanceof EntityPlayer) {
                e.addCapability(new ResourceLocation(Pubgmc.MOD_ID, "playerdata"), new PlayerDataProvider());
            }
        }

        @SubscribeEvent
        public static void onRespawn(PlayerEvent.PlayerRespawnEvent e) {
            getCap(e.player).sync(e.player);
        }

        @SubscribeEvent
        public static void clone(net.minecraftforge.event.entity.player.PlayerEvent.Clone e) {
            Capability.IStorage storage = PlayerDataProvider.PLAYER_DATA.getStorage();
            IPlayerData oldData = getCap(e.getOriginal());
            IPlayerData newData = getCap(e.getEntityPlayer());
            NBTTagCompound nbt = (NBTTagCompound) storage.writeNBT(PlayerDataProvider.PLAYER_DATA, oldData, null);
            storage.readNBT(PlayerDataProvider.PLAYER_DATA, newData, null, nbt);
            getCap(e.getEntityPlayer()).sync(e.getEntityPlayer());
        }

        @SubscribeEvent
        public static void onDimensionChanged(PlayerEvent.PlayerChangedDimensionEvent e) {
            getCap(e.player).sync(e.player);
        }

        @SubscribeEvent
        public static void onStartTracking(net.minecraftforge.event.entity.player.PlayerEvent.StartTracking event) {
            if(event.getTarget() instanceof EntityPlayerMP) {
                EntityPlayer player = (EntityPlayer) event.getTarget();
                PacketHandler.sendToClient(new PacketClientCapabilitySync(player, PlayerData.get(player).serializeNBT()), (EntityPlayerMP) event.getEntityPlayer());
            }
        }

        public static IPlayerData getCap(EntityPlayer p) {
            if(p.hasCapability(PlayerDataProvider.PLAYER_DATA, null)) {
                return p.getCapability(PlayerDataProvider.PLAYER_DATA, null);
            } else throw new IllegalStateException("[PUBGMC] Couldn't get player data for " + p.getName());
        }
    }
}
