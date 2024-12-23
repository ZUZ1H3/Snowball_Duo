import java.io.Serializable;

class ChatMsg implements Serializable {
    private static final long serialVersionUID = 1L;
    public String code;
    public String UserName;
    public String data;
    public int objIdx;
    public String objType;

    public ChatMsg(String UserName, String code, String msg) {
        this.code = code;
        this.UserName = UserName;
        this.data = msg;
    }

    public ChatMsg(String code, int objIdx, String objType) {
        this.code = code;
        this.objIdx = objIdx;
        this.objType = objType;

    }

    public String getUserName() {
        return UserName;
    }

    public String getData() {
        return data;
    }
}