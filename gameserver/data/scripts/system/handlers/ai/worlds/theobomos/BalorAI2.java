package ai.worlds.theobomos;

import ai.AggressiveNpcAI2;

import com.ne.gs.utils.ThreadPoolManager;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.AbstractAI;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.EmotionType;
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.world.WorldPosition;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;


@AIName("balor")
public class BalorAI2 extends AggressiveNpcAI2 {

	private int stage1 = 0;
	private int stage2 = 0;
	private int stage3 = 0;
	
    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        checkPercentage(getLifeStats().getHpPercentage());
    }

    @Override
    protected void handleDied() {
        super.handleDied();
		sendMsg(341551);
		stage1 = 0;
		stage2 = 0;
		stage3 = 0;
		despawn(280956);
		despawn(280957);
		despawn(280955);
		despawn(280954);
    }

    @Override
    protected void handleBackHome() {
        super.handleBackHome();
		sendMsg(341552);
		stage1 = 0;
		stage2 = 0;
		stage3 = 0;
		despawn(280956);
		despawn(280957);
		despawn(280955);
		despawn(280954);
    }
	
	private void checkPercentage(int hpPercentage) {
        float direction = Rnd.get(0, 199) / 100f;
        int distance = Rnd.get(3, 6);
        float x1 = (float) (Math.cos(Math.PI * direction) * distance);
        float y1 = (float) (Math.sin(Math.PI * direction) * distance);
        WorldPosition p = getPosition();
        if (hpPercentage <= 75) {
			stage1++;
			if (stage1 == 1) {
				sendMsg(341553);
				attackPlayer((Npc) spawn(280956,  p.getX() + x1, p.getY() + y1, p.getZ(), (byte)0));
			}
        }
        if (hpPercentage <= 50) {
			stage2++;
			if (stage2 == 1) {
				sendMsg(341554);
				attackPlayer((Npc) spawn(280957,  p.getX() + x1, p.getY() + y1, p.getZ(), (byte)0));
			}
        }
		if (hpPercentage <= 25) {
			stage3++;
			if (stage3 == 1) {
				sendMsg(341557);
				attackPlayer((Npc) spawn(280955,  p.getX() + x1, p.getY() + y1, p.getZ(), (byte)0));
				attackPlayer((Npc) spawn(280954,  p.getX() + x1, p.getY() + y1, p.getZ(), (byte)0));
			}
        }
    }
	
	private void attackPlayer(final Npc npc) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                npc.setTarget(getTarget());
                ((AbstractAI) npc.getAi2()).setStateIfNot(AIState.WALKING);
                npc.setState(1);
                npc.getMoveController().moveToTargetObject();
                PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
            }
        }, 1000);
    }
	
	private void despawn(int npcId) {
	  for (Npc npc : getPosition().getWorldMapInstance().getNpcs(npcId)) {
		 npc.getController().onDelete();
	  }
	}

	private void sendMsg(int msg) {
        NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, 0);
    }
}