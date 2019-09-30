package quest.gelkmaros_armor;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Romanz
 */
public class _21054MissionofDestiny extends QuestHandler {

    private final static int questId = 21054;

    public _21054MissionofDestiny() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(799318).addOnQuestStart(questId);
        qe.registerQuestNpc(799318).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
            if (targetId == 799318) {
                switch (dialog) {
                    case START_DIALOG:
                        return sendQuestDialog(env, 1011);
                    case SELECT_ACTION_1012: {
                        return sendQuestDialog(env, 1012);
                    }
                    case ASK_ACCEPTION: {
                        return sendQuestDialog(env, 4);
                    }
                    case ACCEPT_QUEST: {
                        return sendQuestStartDialog(env);
                    }
                    case REFUSE_QUEST: {
                        return sendQuestDialog(env, 1004);
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (targetId == 799318) {
                switch (dialog) {
                    case START_DIALOG: {
                        return sendQuestDialog(env, 2375);
                    }
                    case SELECT_ACTION_2034: {
                        return sendQuestDialog(env, 2034);
                    }
                    case CHECK_COLLECTED_ITEMS: {
                        return checkQuestItems(env, var, var, true, 5, 2716);
                    }
                    case FINISH_DIALOG: {
                        return sendQuestSelectionDialog(env);
                    }

                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 799318) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
