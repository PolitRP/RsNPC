package com.smallaswater.npc.entitys;

import cn.nukkit.Player;
import cn.nukkit.entity.data.EntityDataTypes;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.SetEntityLinkPacket;
import cn.nukkit.network.protocol.types.EntityLink;
import cn.nukkit.registry.Registries;
import com.smallaswater.npc.data.RsNpcConfig;
import com.smallaswater.npc.variable.VariableManage;
import lombok.NonNull;

/**
 * 基于自定义实体功能实现的RsNPC实体
 *
 * @author LT_Name
 */
public class EntityRsNPCCustomEntity extends EntityRsNPC {

    private String identifier;

    @Deprecated
    public EntityRsNPCCustomEntity(IChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityRsNPCCustomEntity(@NonNull IChunk chunk, @NonNull CompoundTag nbt, RsNpcConfig config) {
        super(chunk, nbt, config);
    }

    @Override
    public int getNetworkId() {
        return Registries.ENTITY.getEntityNetworkId(this.identifier);
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public void setSkinId(int skinId) {
        this.namedTag.putInt("skinId", skinId);
        this.setDataProperty(
                EntityDataTypes.SKIN_ID,
                this.namedTag.getInt("skinId")
        );
    }

    public int getSkinId() {
        return this.namedTag.getInt("skinId");
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setDataProperty(
                EntityDataTypes.SKIN_ID,
                this.namedTag.getInt("skinId")
        );
    }

    @Override
    public void addMovement(double x, double y, double z, double yaw, double pitch, double headYaw) {
        this.level.addEntityMovement(this, x, y, z, yaw, pitch, headYaw);
    }

    @Override
    public void spawnTo(Player player) {
        if (!this.hasSpawned.containsKey(player.getLoaderId()) && this.chunk != null && player.getUsedChunks().contains(Level.chunkHash(this.chunk.getX(), this.chunk.getZ()))) {
            this.hasSpawned.put(player.getLoaderId(), player);
            player.dataPacket(createAddEntityPacket(player));
        }

        if (this.riding != null) {
            this.riding.spawnTo(player);

            SetEntityLinkPacket pk = new SetEntityLinkPacket();
            pk.vehicleUniqueId = this.riding.getId();
            pk.riderUniqueId = this.getId();
            pk.type = EntityLink.Type.RIDER;
            pk.immediate = 1;

            player.dataPacket(pk);
        }
    }

    @Override
    public DataPacket createAddEntityPacket() {
        AddEntityPacket addEntity = new AddEntityPacket();
        addEntity.type = this.getNetworkId();
        addEntity.entityUniqueId = this.getId();
        addEntity.id = this.getIdentifier();
        addEntity.entityRuntimeId = this.getId();
        addEntity.yaw = (float) this.yaw;
        addEntity.headYaw = (float) this.yaw;
        addEntity.pitch = (float) this.pitch;
        addEntity.x = (float) this.x;
        addEntity.y = (float) this.y + this.getBaseOffset();
        addEntity.z = (float) this.z;
        addEntity.speedX = (float) this.motionX;
        addEntity.speedY = (float) this.motionY;
        addEntity.speedZ = (float) this.motionZ;
        addEntity.entityData = this.entityDataMap;

        addEntity.links = new EntityLink[this.passengers.size()];
        for (int i = 0; i < addEntity.links.length; i++) {
            addEntity.links[i] = new EntityLink(this.getId(), this.passengers.get(i).getId(), i == 0 ? EntityLink.Type.RIDER : EntityLink.Type.PASSENGER, false, false);
        }

        return addEntity;
    }

    public DataPacket createAddEntityPacket(Player player) {
        AddEntityPacket pk = (AddEntityPacket) this.createAddEntityPacket();
        pk.entityData.putType(
                EntityDataTypes.NAME,
                VariableManage.stringReplace(player, this.getNameTag(), this.getConfig())
        );
        return pk;
    }

    @Override
    public void setSkin(Skin skin) {
        this.skin = skin;
    }

}
