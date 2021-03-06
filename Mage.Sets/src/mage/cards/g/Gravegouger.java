
package mage.cards.g;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.common.LeavesBattlefieldTriggeredAbility;
import mage.abilities.effects.Effect;
import mage.abilities.effects.common.ExileTargetForSourceEffect;
import mage.abilities.effects.common.ReturnFromExileForSourceEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.constants.Zone;
import mage.filter.FilterCard;
import mage.target.common.TargetCardInASingleGraveyard;

/**
 *
 * @author LoneFox
 */
public final class Gravegouger extends CardImpl {

    public Gravegouger(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId,setInfo,new CardType[]{CardType.CREATURE},"{2}{B}");
        this.subtype.add(SubType.NIGHTMARE);
        this.subtype.add(SubType.HORROR);
        this.power = new MageInt(2);
        this.toughness = new MageInt(2);

        // When Gravegouger enters the battlefield, exile up to two target cards from a single graveyard.
        Effect effect = new ExileTargetForSourceEffect();
        effect.setText("exile up to two target cards from a single graveyard");
        Ability ability = new EntersBattlefieldTriggeredAbility(effect, false);
        ability.addTarget(new TargetCardInASingleGraveyard(0, 2, new FilterCard("cards from a single graveyard")));
        this.addAbility(ability);
        // When Gravegouger leaves the battlefield, return the exiled cards to their owner's graveyard.
        this.addAbility(new LeavesBattlefieldTriggeredAbility(new ReturnFromExileForSourceEffect(Zone.GRAVEYARD), false));
    }

    public Gravegouger(final Gravegouger card) {
        super(card);
    }

    @Override
    public Gravegouger copy() {
        return new Gravegouger(this);
    }
}
