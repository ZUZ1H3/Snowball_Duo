
import java.io.Serializable;
import javax.swing.ImageIcon;

class ChatMsg implements Serializable {
    private static final long serialVersionUID = 1L;
    private String UserName;
    private String code; // 100:로그인, 400:로그아웃, 200:채팅메시지, 300:Image
    private String data;
    private int objIdx;
    private String objType;
    public ImageIcon img;

    public ChatMsg(String UserName, String code, String data) {
        this.UserName = UserName;
        this.code = code;
        this.data = data;
    }

    @Override
    public String toString() {
        return "ChatMsg [UserName=" + UserName + ", code=" + code + ", data=" + data + "]";
    }

    public ChatMsg(String code, int itemIdx, String objType) {
        this.code = code;
        this.objIdx = itemIdx;
        this.objType = objType;
    }

    public ChatMsg(String UserName, String code) {
        this.UserName = UserName;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public String getUserName() {
        return UserName;
    }

    public int getObjIdx() {
        return objIdx;
    }

    public String getObjType() {
        return objType;
    }

    public void setId(String UserName) {
        this.UserName = UserName;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setImg(ImageIcon img) {
        this.img = img;
    }
}