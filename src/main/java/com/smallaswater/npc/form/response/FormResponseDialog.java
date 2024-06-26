package com.smallaswater.npc.form.response;

import cn.nukkit.network.protocol.NPCRequestPacket;
import com.smallaswater.npc.form.element.ResponseElementDialogButton;
import com.smallaswater.npc.form.windows.AdvancedFormWindowDialog;
import lombok.Getter;

@Getter
public class FormResponseDialog {

    private final long entityRuntimeId;
    private final String data;
    private final ResponseElementDialogButton clickedButton; // может быть null
    private final String sceneName;
    private final NPCRequestPacket.RequestType requestType;
    private final int actionType;

    public FormResponseDialog(NPCRequestPacket packet, AdvancedFormWindowDialog dialog) {
        this.entityRuntimeId = packet.entityRuntimeId;
        this.data = packet.data;
        ResponseElementDialogButton button = null;
        if (packet.requestType == NPCRequestPacket.RequestType.EXECUTE_ACTION) {
            try {
                button = dialog.getButtons().get(packet.skinType);
            } catch (IndexOutOfBoundsException e) {
                // button remains null if index is out of bounds
            }
        }
        this.clickedButton = button;
        this.sceneName = packet.sceneName;
        this.requestType = packet.requestType;
        this.actionType = packet.skinType;
    }
}
