package chat;

public class UserMessage {
    private String name;
    private String content;
    private String date;
    private String recipient;
    public UserMessage(){}
    public UserMessage(String name, String content, String date){
        this.name = name;
        this.content = content;
        this.date = date;
    }
    public UserMessage(String name, String content, String date, String recipient){
        this.name = name;
        this.content = content;
        this.date = date;
        this.recipient=recipient;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getContent(){return content;}
    public void setContent(String content){this.content = content;}
    public String getDate(){return this.date;}
    public void setDate(String date){this.date=date;}
    public String getRecipient(){return this.recipient;}
    public void setRecipient(String receRecipient){
        this.recipient=receRecipient;
    }
    @Override
    public String toString() {
        return "{" +
                "name='"+name+"'" +
                "content='"+content+"'" +
                "date='"+date+"'" +
                "}";
    }
}
