package net.runelite.client.plugins.socket.packet;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Event triggered when the list of party members is updated.
 */
@AllArgsConstructor
public class SocketMembersUpdate
{

    @Getter(AccessLevel.PUBLIC)
    private List<String> members;

}