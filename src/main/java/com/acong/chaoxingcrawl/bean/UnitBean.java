package com.acong.chaoxingcrawl.bean;

import java.util.List;

/**
 * 单位/学校集合
 */
public class UnitBean {

    /**
     * result : true
     * fromNums : 8
     * froms : [{"dxfid":631,"domain":"ptu.benke.chaoxing.com","name":"莆田学院","pid":0,"id":2134,"allowJoin":0},{"dxfid":6234,"domain":"ptxyfsyy.yz.chaoxing.com","name":"莆田学院附属医院","pid":0,"id":6963,"allowJoin":0},{"dxfid":38678,"domain":"ptxyfssyxx.jichu.chaoxing.com","name":"莆田学院附属实验小学","pid":0,"id":41267,"allowJoin":0},{"dxfid":18186,"domain":"cf722978-c0f8-4e21-a442-f42a27822670","name":"《莆田学院学报》编辑部","pid":0,"id":17605,"allowJoin":0},{"dxfid":631,"domain":"ptuydjxds.fanya.chaoxing.com","name":"莆田学院\u201c超星杯\u201d移动教学大赛","allianceName":"","pid":0,"id":45657,"allowJoin":0},{"dxfid":631,"domain":"ptumks.fanya.chaoxing.com","name":"莆田学院马克思主义学院","allianceName":"","pid":0,"id":61863,"allowJoin":0},{"dxfid":38901,"domain":"ptsjsjxxy.jichu.chaoxing.com","name":"莆田市教师进修学院","pid":0,"id":41500,"allowJoin":0},{"dxfid":36961,"domain":"ptsjsjxxyfx.jichu.chaoxing.com","name":"莆田市教师进修学院附属小学","pid":0,"id":39333,"allowJoin":0}]
     */

    private boolean result;
    private int fromNums;
    private List<FromsBean> froms;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public int getFromNums() {
        return fromNums;
    }

    public void setFromNums(int fromNums) {
        this.fromNums = fromNums;
    }

    public List<FromsBean> getFroms() {
        return froms;
    }

    public void setFroms(List<FromsBean> froms) {
        this.froms = froms;
    }

    public static class FromsBean {
        /**
         * dxfid : 631
         * domain : ptu.benke.chaoxing.com
         * name : 莆田学院
         * pid : 0
         * id : 2134
         * allowJoin : 0
         * allianceName :
         */

        private int dxfid;
        private String domain;
        private String name;
        private int pid;
        private int id;
        private int allowJoin;
        private String allianceName;

        public int getDxfid() {
            return dxfid;
        }

        public void setDxfid(int dxfid) {
            this.dxfid = dxfid;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getPid() {
            return pid;
        }

        public void setPid(int pid) {
            this.pid = pid;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getAllowJoin() {
            return allowJoin;
        }

        public void setAllowJoin(int allowJoin) {
            this.allowJoin = allowJoin;
        }

        public String getAllianceName() {
            return allianceName;
        }

        public void setAllianceName(String allianceName) {
            this.allianceName = allianceName;
        }
    }
}
