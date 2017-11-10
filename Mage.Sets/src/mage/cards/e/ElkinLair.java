/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */
package mage.cards.e;

import java.util.Set;
import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.DelayedTriggeredAbility;
import mage.abilities.common.BeginningOfUpkeepTriggeredAbility;
import mage.abilities.common.delayed.AtTheBeginOfNextEndStepDelayedTriggeredAbility;
import mage.abilities.effects.AsThoughEffectImpl;
import mage.abilities.effects.ContinuousEffect;
import mage.abilities.effects.OneShotEffect;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.AsThoughEffectType;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.SuperType;
import mage.constants.TargetController;
import mage.constants.Zone;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.filter.FilterCard;
import mage.players.Player;
import mage.target.targetpointer.FixedTarget;
import mage.util.RandomUtil;

/**
 *
 * @author L_J
 */
public class ElkinLair extends CardImpl {

    public ElkinLair(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ENCHANTMENT}, "{3}{R}");
        addSuperType(SuperType.WORLD);

        // At the beginning of each player's upkeep, that player exiles a card at random from his or her hand. The player may play that card this turn. At the beginning of the next end step, if the player hasn't played the card, he or she puts it into his or her graveyard.
        this.addAbility(new BeginningOfUpkeepTriggeredAbility(new ElkinLairUpkeepEffect(), TargetController.ANY, false));

    }

    public ElkinLair(final ElkinLair card) {
        super(card);
    }

    @Override
    public ElkinLair copy() {
        return new ElkinLair(this);
    }

}

class ElkinLairUpkeepEffect extends OneShotEffect {

    public ElkinLairUpkeepEffect() {
        super(Outcome.Benefit);
        this.staticText = "that player exiles a card at random from his or her hand. The player may play that card this turn. At the beginning of the next end step, if the player hasn't played the card, he or she puts it into his or her graveyard";
    }

    public ElkinLairUpkeepEffect(final ElkinLairUpkeepEffect effect) {
        super(effect);
    }

    @Override
    public ElkinLairUpkeepEffect copy() {
        return new ElkinLairUpkeepEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(game.getActivePlayerId());
        Permanent sourcePermanent = game.getPermanentOrLKIBattlefield(source.getSourceId());
        if (player != null && sourcePermanent != null) {
            Card[] cards = player.getHand().getCards(new FilterCard(), game).toArray(new Card[0]);
            if (cards.length > 0) {
                Card card = cards[RandomUtil.nextInt(cards.length)];
                if (card != null) {
                    String exileName = sourcePermanent.getIdName() + " <this card may be played the turn it was exiled";
                    player.moveCardsToExile(card, source, game, true, source.getSourceId(), exileName);
                    if (game.getState().getZone(card.getId()) == Zone.EXILED) {
                        ContinuousEffect effect = new ElkinLairPlayExiledEffect(Duration.EndOfTurn);
                        effect.setTargetPointer(new FixedTarget(card.getId(), card.getZoneChangeCounter(game)));
                        game.addEffect(effect, source);

                        DelayedTriggeredAbility delayed = new AtTheBeginOfNextEndStepDelayedTriggeredAbility(new ElkinLairPutIntoGraveyardEffect());
                        game.addDelayedTriggeredAbility(delayed, source);
                    }
                }
                return true;
            }
        }
        return false;
    }
}

class ElkinLairPlayExiledEffect extends AsThoughEffectImpl {

    public ElkinLairPlayExiledEffect(Duration duration) {
        super(AsThoughEffectType.PLAY_FROM_NOT_OWN_HAND_ZONE, duration, Outcome.Benefit);
        staticText = "The player may play that card this turn";
    }

    public ElkinLairPlayExiledEffect(final ElkinLairPlayExiledEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        return true;
    }

    @Override
    public ElkinLairPlayExiledEffect copy() {
        return new ElkinLairPlayExiledEffect(this);
    }

    @Override
    public boolean applies(UUID objectId, Ability source, UUID affectedControllerId, Game game) {
        Card card = game.getCard(objectId);
        if (card != null
                && affectedControllerId.equals(card.getOwnerId())
                && game.getState().getZone(card.getId()) == Zone.EXILED) {
            return true;
        }
        return false;
    }
}

class ElkinLairPutIntoGraveyardEffect extends OneShotEffect {

    public ElkinLairPutIntoGraveyardEffect() {
        super(Outcome.Neutral);
        staticText = "if the player hasn't played the card, he or she puts it into his or her graveyard";
    }

    public ElkinLairPutIntoGraveyardEffect(final ElkinLairPutIntoGraveyardEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(game.getActivePlayerId());
        if (player != null) {
            Set<Card> cardsInExile = game.getExile().getExileZone(source.getSourceId()).getCards(game);
            if (cardsInExile != null) {
                player.moveCardsToGraveyardWithInfo(cardsInExile, source, game, Zone.EXILED);
                return true;
            }
        }
        return false;
    }

    @Override
    public ElkinLairPutIntoGraveyardEffect copy() {
        return new ElkinLairPutIntoGraveyardEffect(this);
    }
}