

public class PlayerInfo {
    int userNum;
    //	Image character;
    String characterImgPath;
    String runRightImgPath;
    String runLeftImgPath;
    String dieImgPath = "GameClient/image/character/die.png";
    State type;
    String jumpLeftImgPath;

    public PlayerInfo() {
        this.type = State.FRONT;
    }

    public void setUserNum(int userNum) {
        this.userNum = userNum;
    }

//	public void setCharacter(Image character) {
//		this.character = character;
//	}

    public void setCharacterImgPath(String characterImgPath) {
        this.characterImgPath = characterImgPath;
    }

    public void setRunRightImgPath(String runRightImgPath) {
        this.runRightImgPath = runRightImgPath;
    }

    public void setRunLeftImgPath(String runLeftImgPath) {
        this.runLeftImgPath = runLeftImgPath;
    }

    public void setState(State type) {
        this.type = type;
    }

    public void setJumpImgPath(String jumpLeftImgPath) {  // 왼쪽 점프 이미지 경로 설정
        this.jumpLeftImgPath = jumpLeftImgPath;
    }

//	public Image getCharacter() {
//	   return character;
//	}

    public String getCharacterImgPath() {
        return characterImgPath;
    }

    public String getRunRightImgPath() {
        return runRightImgPath;
    }

    public String getRunLeftImgPath() {
        return runLeftImgPath;
    }

    public String getDieImgPath() {
        return dieImgPath;
    }

    public String getJumpImgPath() {
        return jumpLeftImgPath;
    }

    public State getState() {
        return type;
    }
}
