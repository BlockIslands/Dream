/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.common.mixin.invalid.api.mcp.entity.passive;

import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import org.spongepowered.api.item.merchant.Merchant;
import org.spongepowered.api.item.merchant.TradeOffer;
import org.spongepowered.api.item.merchant.TradeOfferListMutator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.common.item.util.TradeOfferUtil;
import java.util.List;
import java.util.Random;

@Mixin(MerchantOffers.class)
public interface EntityVillager_ITradeListMixin_API extends TradeOfferListMutator, VillagerEntity.ITradeList {

    @Override
    default void accept(final Merchant owner, final List<TradeOffer> tradeOffers, final Random random) {
        final MerchantOffers tempList = new MerchantOffers();
        for (final TradeOffer offer : tradeOffers) {
            tempList.add(TradeOfferUtil.toNative(offer));
        }
        addMerchantRecipe((IMerchant) owner, tempList, random);
        tradeOffers.clear();
        for (final MerchantOffer recipe : tempList) {
            tradeOffers.add(TradeOfferUtil.fromNative(recipe));
        }
    }

}