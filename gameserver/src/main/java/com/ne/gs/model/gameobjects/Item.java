/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.model.gameobjects;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.ne.gs.model.items.*;
import com.ne.gs.model.stats.calc.functions.StatFunction;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ne.commons.Sys;
import com.ne.commons.annotations.NotNull;
import com.ne.commons.utils.Rnd;
import com.ne.commons.utils.collections.Array;
import com.ne.gs.configs.main.MembershipConfig;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.DescId;
import com.ne.gs.model.IExpirable;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.items.storage.IStorage;
import com.ne.gs.model.items.storage.ItemStorage;
import com.ne.gs.model.items.storage.StorageType;
import com.ne.gs.model.stats.calc.StatOwner;
import com.ne.gs.model.templates.item.EquipType;
import com.ne.gs.model.templates.item.Improvement;
import com.ne.gs.model.templates.item.ItemQuality;
import com.ne.gs.model.templates.item.ItemTemplate;
import com.ne.gs.model.templates.item.ItemType;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.utils.ExpirableManager;
import com.ne.gs.world.World;

/**
 * @author ATracer, Wakizashi, xTz
 */
public class Item extends AionObject implements IExpirable, StatOwner {

    private static final Logger log = LoggerFactory.getLogger(Item.class);

    private long itemCount = 1;
    private int itemColor;
    private int colorExpireTime;
    private String itemCreator;
    private final ItemTemplate itemTemplate;
    private ItemTemplate itemSkinTemplate;
    private ItemTemplate fusionedItemTemplate;
    private boolean isEquipped;
    private int equipmentSlot = ItemStorage.FIRST_AVAILABLE_SLOT;
    private PersistentState persistentState;
    private Set<ManaStone> manaStones;
    private Set<ManaStone> fusionStones;
    private int optionalSocket;
    private int optionalFusionSocket;
    private GodStone godStone;
    private IdianStone idianStone;
    private boolean isSoulBound;
    private int itemLocation;
    private int enchantLevel;
    private int expireTime;
    private volatile ExchangeTime _exchangeTime = ExchangeTimeImpl.DUMMY;
    private long repurchasePrice;
    private int activationCount;
    private ChargeInfo conditioningInfo;
    private Integer _ownerId; // WARN unreliable property, just quick hack for ExchangeTime

    // 4.0
    private int bonusNumber;
    private List<StatFunction> currentModifiers;
    private RandomStats randomStats;
    private boolean configured;

    /**
     * Create simple item with minimum information
     */
    public Item(int objId, ItemTemplate itemTemplate) {
        super(objId);
        this.itemTemplate = itemTemplate;
        if (itemTemplate.isWeapon() || itemTemplate.isArmor()) {
            optionalSocket = Rnd.get(0, itemTemplate.getOptionSlotBonus());
        }
        activationCount = itemTemplate.getActivationCount();
        if (itemTemplate.getExpireTime() != 0) {
            expireTime = (int) (System.currentTimeMillis() / 1000) + itemTemplate.getExpireTime() * 60 - 1;
        }
        persistentState = PersistentState.NEW;
        updateChargeInfo(0);
    }

    /**
     * This constructor should be called from ItemService for newly created items and loadedFromDb
     */
    public Item(int objId, ItemTemplate itemTemplate, long itemCount, boolean isEquipped, int equipmentSlot) {
        this(objId, itemTemplate);
        this.itemCount = itemCount;
        this.isEquipped = isEquipped;
        this.equipmentSlot = equipmentSlot;
    }

    /**
     * This constructor should be called only from DAO while loading from DB
     */

    public Item(int objId, int itemId, long itemCount, int itemColor, int colorExpireTime, String itemCreator, int expireTime,
                int activationCount, boolean isEquipped, boolean isSoulBound, int equipmentSlot, int itemLocation, int enchant,
                int itemSkin, int fusionedItem, int optionalSocket, int optionalFusionSocket, int charge, int randomBonus) {
        super(objId);

        itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
        this.itemCount = itemCount;
        this.itemColor = itemColor;

        // LMFAOOWN fix
        this.colorExpireTime = colorExpireTime;
        this.itemCreator = itemCreator;
        this.expireTime = expireTime;
        this.activationCount = activationCount;
        this.isEquipped = isEquipped;
        this.isSoulBound = isSoulBound;
        this.equipmentSlot = equipmentSlot;
        this.itemLocation = itemLocation;
        this.enchantLevel = enchant;
        this.fusionedItemTemplate = DataManager.ITEM_DATA.getItemTemplate(fusionedItem);
        this.itemSkinTemplate = DataManager.ITEM_DATA.getItemTemplate(itemSkin);
        this.optionalSocket = optionalSocket;
        this.optionalFusionSocket = optionalFusionSocket;
        this.bonusNumber = randomBonus;

        if (itemTemplate.getRandomBonusId() != 0) {
            if (bonusNumber > 0) {
                randomStats = new RandomStats(itemTemplate.getRandomBonusId(), bonusNumber);
                this.configured = true;
            }
        }

        if (fusionedItemTemplate != null) {
            if (!itemTemplate.isCanFuse() || !itemTemplate.isTwoHandWeapon() || !fusionedItemTemplate
                    .isCanFuse() || !fusionedItemTemplate.isTwoHandWeapon()) {
                fusionedItemTemplate = null;
                this.optionalFusionSocket = 0;
            }
        }
        updateChargeInfo(charge);
    }

    public Item(int objId, int itemId, long itemCount, int itemColor, String itemCreator, int expireTime,
                int activationCount, boolean isEquipped, boolean isSoulBound, int equipmentSlot, int itemLocation, int enchant,
                int itemSkin, int fusionedItem, int optionalSocket, int optionalFusionSocket, int charge) {
        super(objId);

        itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
        this.itemCount = itemCount;
        this.itemColor = itemColor;
        this.itemCreator = itemCreator;
        this.expireTime = expireTime;
        this.activationCount = activationCount;
        this.isEquipped = isEquipped;
        this.isSoulBound = isSoulBound;
        this.equipmentSlot = equipmentSlot;
        this.itemLocation = itemLocation;
        this.enchantLevel = enchant;
        this.fusionedItemTemplate = DataManager.ITEM_DATA.getItemTemplate(fusionedItem);
        this.itemSkinTemplate = DataManager.ITEM_DATA.getItemTemplate(itemSkin);
        this.optionalSocket = optionalSocket;
        this.optionalFusionSocket = optionalFusionSocket;
        this.bonusNumber = 0;

        if (itemTemplate.getRandomBonusId() != 0) {
            if (bonusNumber > 0) {
                randomStats = new RandomStats(itemTemplate.getRandomBonusId(), bonusNumber);
                this.configured = true;
            }
        }

        if (fusionedItemTemplate != null) {
            if (!itemTemplate.isCanFuse() || !itemTemplate.isTwoHandWeapon() || !fusionedItemTemplate
                .isCanFuse() || !fusionedItemTemplate.isTwoHandWeapon()) {
                fusionedItemTemplate = null;
                this.optionalFusionSocket = 0;
            }
        }
        updateChargeInfo(charge);
    }

    public void setOwnerId(Integer ownerId) {
        _ownerId = ownerId;
    }

    public Integer getOwnerId() {
        return _ownerId;
    }

    private void updateChargeInfo(int charge) {
        int chargeLevel = getChargeLevelMax();
        if (conditioningInfo == null && chargeLevel > 0) {
            conditioningInfo = new ChargeInfo(charge, this);
        }
        // when break fusioned item and second item has conditioned info - set to null
        if (conditioningInfo != null && chargeLevel == 0) {
            conditioningInfo = null;
        }
    }

    @Override
    public String getName() {
        // TODO
        // item description should return probably string and not id
        return String.valueOf(itemTemplate.getNameId());
    }

    /**
     * @return itemCreator
     */
    public String getItemCreator() {
        if (itemCreator == null) {
            return StringUtils.EMPTY;
        }
        return itemCreator;
    }

    /**
     * @param itemCreator
     *     the itemCreator to set
     */
    public void setItemCreator(String itemCreator) {
        this.itemCreator = itemCreator;
    }

    public String getItemName() {
        return itemTemplate.getName();
    }

    public int getOptionalSocket() {
        return optionalSocket;
    }

    public void setOptionalSocket(int optionalSocket) {
        this.optionalSocket = optionalSocket;
    }

    public boolean hasOptionalSocket() {
        return optionalSocket != 0;
    }

    public int getOptionalFusionSocket() {
        return optionalFusionSocket;
    }

    public boolean hasOptionalFusionSocket() {
        return optionalFusionSocket != 0;
    }

    public void setOptionalFusionSocket(int optionalFusionSocket) {
        this.optionalFusionSocket = optionalFusionSocket;
    }

    /**
     * @return the itemTemplate
     */
    public ItemTemplate getItemTemplate() {
        return itemTemplate;
    }

    /**
     * @return the itemAppearanceTemplate
     */
    public ItemTemplate getItemSkinTemplate() {
        if (itemSkinTemplate == null) {
            return itemTemplate;
        }
        return itemSkinTemplate;
    }

    public void setItemSkinTemplate(ItemTemplate newTemplate) {
        itemSkinTemplate = newTemplate;
        setPersistentState(PersistentState.UPDATE_REQUIRED);
    }

    /**
     * @return the itemColor
     */
    public int getItemColor() {
        return itemColor;
    }

    /**
     * @param itemColor
     *     the itemColor to set
     */
    public void setItemColor(int itemColor) {
        this.itemColor = itemColor;
        setPersistentState(PersistentState.UPDATE_REQUIRED);
    }

    /**
     * @return the itemCount Number of this item in stack. Should be not more than template maxstackcount ?
     */
    public long getItemCount() {
        return itemCount;
    }

    public long getFreeCount() {
        return itemTemplate.getMaxStackCount() - itemCount;
    }

    /**
     * @param itemCount
     *     the itemCount to set
     */
    public void setItemCount(long itemCount) {
        this.itemCount = itemCount;
        setPersistentState(PersistentState.UPDATE_REQUIRED);
    }

    /**
     * This method should be called ONLY from Storage class In all other ways it is not guaranteed to be udpated in a regular update service It is allowed to
     * use this method for newly created items which are not yet in any storage
     *
     * @param count
     *     count
     */
    public long increaseItemCount(long count) {
        if (count <= 0) {
            return 0;
        }
        long cap = itemTemplate.getMaxStackCount();
        long addCount = itemCount + count > cap ? cap - itemCount : count;
        if (addCount != 0) {
            itemCount += addCount;
            setPersistentState(PersistentState.UPDATE_REQUIRED);
        }
        return count - addCount;
    }

    /**
     * This method should be called ONLY from Storage class In all other ways it is not guaranteed to be udpated in a regular update service It is allowed to
     * use this method for newly created items which are not yet in any storage
     *
     * @param count
     *     count
     */
    public long decreaseItemCount(long count) {
        if (count <= 0) {
            return 0;
        }
        long removeCount = count >= itemCount ? itemCount : count;
        itemCount -= removeCount;
        if (itemCount == 0 && !itemTemplate.isKinah()) {
            setPersistentState(PersistentState.DELETED);
        } else {
            setPersistentState(PersistentState.UPDATE_REQUIRED);
        }
        return count - removeCount;
    }

    /**
     * @return the isEquipped
     */
    public boolean isEquipped() {
        return isEquipped;
    }

    /**
     * @param isEquipped
     *     the isEquipped to set
     */
    public void setEquipped(boolean isEquipped) {
        this.isEquipped = isEquipped;
        setPersistentState(PersistentState.UPDATE_REQUIRED);
    }

    /**
     * @return the equipmentSlot Equipment slot can be of 2 types - one is the ItemSlot enum type if equipped, second - is position in cube
     */
    public int getEquipmentSlot() {
        return equipmentSlot;
    }

    /**
     * @param equipmentSlot
     *     the equipmentSlot to set
     */
    public void setEquipmentSlot(int equipmentSlot) {
        this.equipmentSlot = equipmentSlot;
        setPersistentState(PersistentState.UPDATE_REQUIRED);
    }

    /**
     * This method should be used to lazy initialize empty manastone list
     *
     * @return the itemStones
     */
    public Set<ManaStone> getItemStones() {
        if (manaStones == null) {
            manaStones = itemStonesCollection();
        }
        return manaStones;
    }

    /**
     * This method should be used to lazy initialize empty manastone list
     *
     * @return the itemStones
     */
    public Set<ManaStone> getFusionStones() {
        if (fusionStones == null) {
            fusionStones = itemStonesCollection();
        }
        return fusionStones;
    }

    public int getFusionStonesSize() {
        if (fusionStones == null) {
            return 0;
        }
        return fusionStones.size();
    }

    public int getItemStonesSize() {
        if (manaStones == null) {
            return 0;
        }
        return manaStones.size();
    }

    private Set<ManaStone> itemStonesCollection() {
        return new TreeSet<>(new Comparator<ManaStone>() {
            @Override
            public int compare(ManaStone o1, ManaStone o2) {
                if (o1.getSlot() == o2.getSlot()) {
                    return 0;
                }
                return o1.getSlot() > o2.getSlot() ? 1 : -1;
            }
        });
    }

    /**
     * Check manastones without initialization
     */
    public boolean hasManaStones() {
        return manaStones != null && manaStones.size() > 0;
    }

    /**
     * Check fusionstones without initialization
     */
    public boolean hasFusionStones() {
        return fusionStones != null && fusionStones.size() > 0;
    }

    public boolean hasGodStone() {
        return godStone != null;
    }

    /**
     * @return the goodStone
     */
    public GodStone getGodStone() {
        return godStone;
    }

    /**
     * @param itemId
     *
     * @return
     */
    public GodStone addGodStone(int itemId) {
        PersistentState state = godStone != null ? PersistentState.UPDATE_REQUIRED : PersistentState.NEW;
        godStone = new GodStone(getObjectId(), itemId, state);
        return godStone;
    }

    /**
     * @param goodStone
     *     the goodStone to set
     */
    public void setGoodStone(GodStone goodStone) {
        godStone = goodStone;
    }

    /**
     * @return the echantLevel
     */
    public int getEnchantLevel() {
        return enchantLevel;
    }

    /**
     * @param enchantLevel
     *     the echantLevel to set
     */
    public void setEnchantLevel(int enchantLevel) {
        this.enchantLevel = enchantLevel;
        setPersistentState(PersistentState.UPDATE_REQUIRED);
    }

    /**
     * @return the persistentState
     */
    public PersistentState getPersistentState() {
        return persistentState;
    }

    /**
     * Possible changes: NEW -> UPDATED NEW -> UPDATE_REQURIED UPDATE_REQUIRED -> DELETED UPDATE_REQUIRED -> UPDATED UPDATED -> DELETED UPDATED ->
     * UPDATE_REQUIRED
     *
     * @param persistentState
     *     the persistentState to set
     */
    public void setPersistentState(PersistentState persistentState) {
        switch (persistentState) {
            case DELETED:
                if (this.persistentState == PersistentState.NEW) {
                    this.persistentState = PersistentState.NOACTION;
                } else {
                    this.persistentState = PersistentState.DELETED;
                }
                break;
            case UPDATE_REQUIRED:
                if (this.persistentState == PersistentState.NEW) {
                    break;
                }
            default:
                this.persistentState = persistentState;
        }

    }

    public void setItemLocation(int storageType) {
        itemLocation = storageType;
        setPersistentState(PersistentState.UPDATE_REQUIRED);
    }

    public int getItemLocation() {
        return itemLocation;
    }

    public int getItemMask() {
        return itemTemplate.getMask();
    }

    public boolean isSoulBound() {
        return isSoulBound;
    }

    private boolean isSoulBound(Player player) {
        return !player.havePermission(MembershipConfig.DISABLE_SOULBIND) && isSoulBound;
    }

    public void setSoulBound(boolean isSoulBound) {
        this.isSoulBound = isSoulBound;
        setPersistentState(PersistentState.UPDATE_REQUIRED);
    }

    public EquipType getEquipmentType() {
        if (itemTemplate.isStigma()) {
            return EquipType.STIGMA;
        }
        return itemTemplate.getEquipmentType();
    }

    @Override
    public String toString() {
        return "Item [itemId=" + itemTemplate
            .getTemplateId() + " equipmentSlot=" + equipmentSlot + ", godStone=" + godStone + ", isEquipped=" + isEquipped
            + ", itemColor=" + itemColor + ", itemCount=" + itemCount + ", itemLocation=" + itemLocation + ", itemTemplate=" + itemTemplate
            + ", manaStones=" + manaStones + ", persistentState=" + persistentState + "]";
    }

    public int getItemId() {
        return itemTemplate.getTemplateId();
    }

    public int getNameID() {
        return itemTemplate.getNameId();
    }

    public boolean hasFusionedItem() {
        return fusionedItemTemplate != null;
    }

    public ItemTemplate getFusionedItemTemplate() {
        return fusionedItemTemplate;
    }

    public int getFusionedItemId() {
        return fusionedItemTemplate != null ? fusionedItemTemplate.getTemplateId() : 0;
    }

    public void setFusionedItem(ItemTemplate itemTemplate) {
        fusionedItemTemplate = itemTemplate;
        updateChargeInfo(0);
    }

    private static int basicSocket(ItemQuality rarity) {
        switch (rarity) {
            case COMMON:
                return 1;
            case RARE:
                return 2;
            case LEGEND:
                return 3;
            case UNIQUE:
                return 4;
            case EPIC:
                return 5;
            default:
                return 1;
        }
    }

    private static int extendedSocket(ItemType type) {
        switch (type) {
            case NORMAL:
                return 0;
            case ABYSS:
                return 2;
            case DRACONIC:
                return 1;
            case DEVANION:
                return 0;
            default:
                return 0;
        }
    }

    public int getSockets(boolean isFusionItem) {
        int numSockets;
        if (itemTemplate.isWeapon() || itemTemplate.isArmor()) {
            if (isFusionItem) {
                ItemTemplate fusedTemp = getFusionedItemTemplate();
                if (fusedTemp == null) {
                    log.error("Item {} with itemId {} has empty fusioned item ", getObjectId(), getItemId());
                    return 0;
                }
                numSockets = basicSocket(fusedTemp.getItemQuality());
                numSockets += extendedSocket(fusedTemp.getItemType());
                numSockets += hasOptionalFusionSocket() ? getOptionalFusionSocket() : 0;
            } else {
                numSockets = basicSocket(getItemTemplate().getItemQuality());
                numSockets += extendedSocket(getItemTemplate().getItemType());
                numSockets += hasOptionalSocket() ? getOptionalSocket() : 0;
            }
            if (numSockets < 6) {
                return numSockets;
            }
            return 6;
        }
        return 0;
    }

    /**
     * @return the mask
     */
    public int getItemMask(Player player) {
        return checkConfig(player, itemTemplate.getMask());
    }

    /**
     * @param player
     *
     * @return
     */
    private int checkConfig(Player player, int mask) {
        int newMask = mask;
        if (player.havePermission(MembershipConfig.STORE_WH_ALL)) {
            newMask = newMask | ItemMask.STORABLE_IN_WH;
        }
        if (player.havePermission(MembershipConfig.STORE_AWH_ALL)) {
            newMask = newMask | ItemMask.STORABLE_IN_AWH;
        }
        if (player.havePermission(MembershipConfig.STORE_LWH_ALL)) {
            newMask = newMask | ItemMask.STORABLE_IN_LWH;
        }
        if (player.havePermission(MembershipConfig.TRADE_ALL)) {
            newMask = newMask | ItemMask.TRADEABLE;
        }
        if (player.havePermission(MembershipConfig.REMODEL_ALL)) {
            newMask = newMask | ItemMask.REMODELABLE;
        }

        return newMask;
    }

    /**
     * Compares two items on their object and item ids
     *
     * @param i
     *     item
     *
     * @return true, if this item is equal to the object item
     *
     * @author vlog
     */
    public boolean isSameItem(Item i) {
        return getObjectId().equals(i.getObjectId()) && getItemId() == i.getItemId();
    }

    public boolean isStorableinWarehouse(Player player) {
        return (getItemMask(player) & ItemMask.STORABLE_IN_WH) == ItemMask.STORABLE_IN_WH;
    }

    public boolean isStorableinAccWarehouse(Player player) {
        return (getItemMask(player) & ItemMask.STORABLE_IN_AWH) == ItemMask.STORABLE_IN_AWH && !isSoulBound(player);
    }

    public boolean isStorableinLegWarehouse(Player player) {
        return (getItemMask(player) & ItemMask.STORABLE_IN_LWH) == ItemMask.STORABLE_IN_LWH && !isSoulBound(player);
    }

    public boolean isTradeable(Player player) {
        return (getItemMask(player) & ItemMask.TRADEABLE) == ItemMask.TRADEABLE && !isSoulBound(player);
    }

    public boolean isRemodelable(Player player) {
        return (getItemMask(player) & ItemMask.REMODELABLE) == ItemMask.REMODELABLE;
    }

    public boolean isSellable() {
        return (getItemMask() & ItemMask.SELLABLE) == ItemMask.SELLABLE;
    }

    /**
     * @return Returns the expireTime.
     */
    @Override
    public int getExpireTime() {
        return expireTime;
    }

    public int getExpireTimeRemaining() {
        if (expireTime == 0) {
            return 0;
        }
        return expireTime - (int) (System.currentTimeMillis() / 1000);
    }

    public ExchangeTime getExchangeTime() {
        return _exchangeTime;
    }

    public void setExchangeTime(@NotNull ExchangeTime exchangeTime) {
        _exchangeTime = exchangeTime;
    }

    @Override
    public void expireEnd(Player player) {
        if (player == null) {
            return;
        }
        if (isEquipped()) {
            player.getEquipment().unEquipItem(getObjectId(), getEquipmentSlot());
        }

        for (StorageType i : StorageType.values()) {
            if (i == StorageType.LEGION_WAREHOUSE) {
                continue;
            }
            IStorage storage = player.getStorage(i.getId());

            if (storage != null && storage.getItemByObjId(getObjectId()) != null) {
                storage.delete(this);
                switch (i) {
                    case CUBE:
                        player.sendPck(new SM_SYSTEM_MESSAGE(1400034, DescId.of(getNameID())));
                        break;
                    case ACCOUNT_WAREHOUSE:
                    case REGULAR_WAREHOUSE:
                        player.sendPck(new SM_SYSTEM_MESSAGE(1400406, DescId.of(getNameID())));
                        break;
                }
            }
        }
    }

    @Override
    public void expireMessage(Player player, int time) {
        if (player != null) {
            player.sendPck(new SM_SYSTEM_MESSAGE(1400481, DescId.of(getNameID()), time));
        }
    }

    public void setRepurchasePrice(long price) {
        repurchasePrice = price;
    }

    public long getRepurchasePrice() {
        return repurchasePrice;
    }

    public int getActivationCount() {
        return activationCount;
    }

    public void setActivationCount(int activationCount) {
        this.activationCount = activationCount;
    }

    public ChargeInfo getConditioningInfo() {
        return conditioningInfo;
    }

    public int getChargePoints() {
        return conditioningInfo != null ? conditioningInfo.getChargePoints() : 0;
    }

    /**
     * Calculate charge level based on main item and fusioned item
     */
    public int getChargeLevel() {
        if (getChargePoints() == 0) {
            return 0;
        }
        return getChargePoints() > ChargeInfo.LEVEL1 ? 2 : 1;
    }

    public int getChargeLevelMax() {
        int thisChargeLevel = 0;
        if (getImprovement() != null) {
            thisChargeLevel = getImprovement().getLevel();
        }
        int fusionedChargeLevel = 0;
        if (hasFusionedItem() && getFusionedItemTemplate().getImprovement() != null) {
            fusionedChargeLevel = getFusionedItemTemplate().getImprovement().getLevel();
        }
        return Math.max(thisChargeLevel, fusionedChargeLevel);
    }

    @Override
    public boolean canExpireNow() {
        return true;
    }

    public Improvement getImprovement() {
        if (getItemTemplate().getImprovement() != null) {
            return getItemTemplate().getImprovement();
        }
        if (hasFusionedItem() && getFusionedItemTemplate().getImprovement() != null) {
            return getFusionedItemTemplate().getImprovement();
        }
        return null;
    }

    public interface ExchangeTime extends ExpirableManager.Expirable {

        boolean isExpired();

        int getRemainingSeconds();

        public void expireNow();
    }

    public IdianStone getIdianStone() {
        return idianStone;
    }

    public void setIdianStone(IdianStone idianStone) {
        this.idianStone = idianStone;
    }

    public RandomStats getRandomStats() {
        return randomStats;
    }

    public void setRandomStats(RandomStats randomStats) {
        this.randomStats = randomStats;
    }

    public void setBonusNumber(int bonusNumber) {
        this.bonusNumber = bonusNumber;
    }

    public boolean isConfigured() {
        return !itemTemplate.isArmor() && !itemTemplate.isWeapon() || this.configured;
    }

    public static final class ExchangeTimeImpl implements ExchangeTime {
        private final ExpirableManager _expireManager;
        private final Item _item;
        private long _expiresAt;

        private ExchangeTimeImpl(ExpirableManager expireManager, Item item, long expiresAt) {
            _expireManager = expireManager;
            _item = item;
            _expiresAt = expiresAt;
        }

        @Override
        public long expiresAt() {
            return _expiresAt;
        }

        @Override
        public boolean isExpired() {
            return _expiresAt - Sys.millis() <= 0;
        }

        @Override
        public int getRemainingSeconds() {
            return Math.max(0, (int) ((_expiresAt - Sys.millis()) / 1000));
        }

        @Override
        public void expire() {
            Player owner = World.getInstance().findPlayer(_item.getOwnerId());
            if (owner != null) {
                owner.sendPck(SM_SYSTEM_MESSAGE.STR_MSG_EXCHANGE_TIME_OVER(_item.getNameID()));
            }
        }

        @Override
        public void expireNow() {
            _expireManager.take(this);
            _item.setExchangeTime(DUMMY);
        }

        public static void schedule(ExpirableManager expireManager, Item item) {
            int duration = item.getItemTemplate().getTempExchangeTime() * 60000;
            long expireTimestamp = Sys.millis() + duration;

            ExchangeTimeImpl e = new ExchangeTimeImpl(expireManager, item, expireTimestamp);
            expireManager.put(e);
            item.setExchangeTime(e);
        }

        public static final ExchangeTime DUMMY = new ExchangeTime() {

            @Override
            public boolean isExpired() { return true; }

            @Override
            public int getRemainingSeconds() { return 0; }

            @Override
            public long expiresAt() { return 0; }

            @Override
            public void expire() { }

            @Override
            public void expireNow() { }
        };
    }

    private static class StoneHolder {
        private final Array<ManaStone> _stones;

        public StoneHolder(int size) {
            _stones = new Array<>(size);
        }

        public StoneHolder(Iterable<ManaStone> stones) {
            _stones = new Array<>(stones);
        }

        public int findEmptySlot() {
            for (int i = 0; i < _stones.getSize(); i++) {
                if (_stones.get(i) == null) {
                    return i;
                }
            }

            return -1;
        }

        public void add(int slot, ManaStone stone) {
            if (slot < 0 || slot > _stones.getSize() - 1) {
                throw new IllegalArgumentException();
            }

            if (_stones.get(slot) != null) {
                throw new IllegalStateException();
            }

            _stones.set(slot, stone);
        }

        public ManaStone remove(int index) {
            ManaStone prev = _stones.get(index);
            _stones.set(index, null);
            return prev;
        }

        public Iterable<ManaStone> getStones() {
            return Iterables.filter(_stones, Predicates.notNull());
        }
    }
}
