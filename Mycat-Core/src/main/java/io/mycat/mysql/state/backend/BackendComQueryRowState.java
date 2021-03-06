package io.mycat.mysql.state.backend;


import java.io.IOException;

import io.mycat.mysql.state.PacketProcessStateTemplete;
import io.mycat.net2.Connection;

import io.mycat.backend.MySQLBackendConnection;
import io.mycat.mysql.packet.MySQLPacket;

/**
 * 行数据传送状态
 *
 * @author ynfeng
 */
public class BackendComQueryRowState extends PacketProcessStateTemplete {
    public static final BackendComQueryRowState INSTANCE = new BackendComQueryRowState();


    private BackendComQueryRowState() {
    }


    @Override
    public boolean handleShortHalfPacket(Connection connection, Object attachment, int packetStartPos) throws IOException {
        MySQLBackendConnection mySQLBackendConnection = (MySQLBackendConnection) connection;
        if (mySQLBackendConnection.getDataBuffer().writableBytes() == 0) {
            mySQLBackendConnection.startTransfer(mySQLBackendConnection.getMySQLFrontConnection(), mySQLBackendConnection.getDataBuffer());
        }
        return false;
    }

    @Override
    public boolean handleLongHalfPacket(Connection connection, Object attachment, int packetStartPos, int packetLen, byte type) throws IOException {
        MySQLBackendConnection mySQLBackendConnection = (MySQLBackendConnection) connection;
        mySQLBackendConnection.startTransfer(mySQLBackendConnection.getMySQLFrontConnection(), mySQLBackendConnection.getDataBuffer());
        return false;
    }

    @Override
    public boolean handleFullPacket(Connection connection, Object attachment, int packetStartPos, int packetLen, byte type) throws IOException {
        MySQLBackendConnection mySQLBackendConnection = (MySQLBackendConnection) connection;
        if (type == MySQLPacket.EOF_PACKET) {
            mySQLBackendConnection.getProtocolStateMachine().setNextState(BackendIdleState.INSTANCE);
            mySQLBackendConnection.startTransfer(mySQLBackendConnection.getMySQLFrontConnection(), mySQLBackendConnection.getDataBuffer());
            interruptIterate();
            return false;
        }
        if (mySQLBackendConnection.getDataBuffer().writableBytes() == 0) {
            mySQLBackendConnection.startTransfer(mySQLBackendConnection.getMySQLFrontConnection(), mySQLBackendConnection.getDataBuffer());
            interruptIterate();
        }
        return false;
    }
}
