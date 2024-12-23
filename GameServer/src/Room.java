import java.net.ServerSocket;
import java.util.Vector;
public class Room extends Thread{
    @SuppressWarnings("unchecked")

    private ServerSocket socket;
    private Vector UserVec; // UserService 벡터
    private Vector UserNameVec; // 참여자 이름 벡터

    // 새로운 참가자 accept() 하고 user thread를 새로 생성한다.
    public Room(ServerSocket socket) {
        this.socket = socket;
        UserVec = new Vector();
        UserNameVec = new Vector();
        System.out.println("UserVec.size()="+UserVec.size());
    }

    public Vector getUserVec() {
        return this.UserVec;
    }

    public Vector getUserNameVec() {
        return this.UserNameVec;
    }

    public boolean enterRoom(String userName) {
        System.out.println("enterRoom");
        if(UserVec.size()==2) {
            System.out.println("2명이 이미 참가 중입니다.");
            return false;
        }
        else {
            System.out.println("게임에 참가합니다.");
            UserNameVec.add(userName);
            return true;
        }
    }
}