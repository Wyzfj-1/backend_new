package com.wsn.powerstrip.communication.POJO.VO;

/**
 * @author hj
 * @date 2019/10/28 20:12
 */
public class SendResult {
    private String msgGroup;
    private String rspcod;
    private String operator;


    public SendResult(String msgGroup, String rspcod, String operator) {
        this.msgGroup = msgGroup;
        this.rspcod = rspcod;
        this.operator = operator;
    }

    public SendResult() {

    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getMsgGroup() {
        return msgGroup;
    }

    public void setMsgGroup(String msgGroup) {
        this.msgGroup = msgGroup;
    }

    public String getRspcod() {
        return rspcod;
    }

    public void setRspcod(String rspcod) {
        this.rspcod = rspcod;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"msgGroup\":\"")
                .append(msgGroup).append('\"');
        sb.append(",\"rspcod\":\"")
                .append(rspcod).append('\"');
        sb.append(",\"operator\":\"")
                .append(operator).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
