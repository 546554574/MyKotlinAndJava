package com.smart.car.ui.model;

import java.util.List;

public class WorkStationVo {

    private List<PlacesBean> places;

    public List<PlacesBean> getPlaces() {
        return places;
    }

    public void setPlaces(List<PlacesBean> places) {
        this.places = places;
    }

    public static class PlacesBean {
        /**
         * group_id : 1
         * group_name : 一轨
         * place : [{"id":12,"name":"工位12","site":{"x":0,"y":2000}},{"id":11,"name":"工位11","site":{"x":0,"y":1000}}]
         */

        private int group_id;
        private String group_name;
        private List<PlaceBean> place;

        public int getGroup_id() {
            return group_id;
        }

        public void setGroup_id(int group_id) {
            this.group_id = group_id;
        }

        public String getGroup_name() {
            return group_name;
        }

        public void setGroup_name(String group_name) {
            this.group_name = group_name;
        }

        public List<PlaceBean> getPlace() {
            return place;
        }

        public void setPlace(List<PlaceBean> place) {
            this.place = place;
        }

        public static class PlaceBean {
            /**
             * id : 12
             * name : 工位12
             * site : {"x":0,"y":2000}
             */

            private int id;
            private String name;
            private SiteBean site;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public SiteBean getSite() {
                return site;
            }

            public void setSite(SiteBean site) {
                this.site = site;
            }

            public static class SiteBean {
                /**
                 * x : 0
                 * y : 2000
                 */

                private int x;
                private int y;

                public int getX() {
                    return x;
                }

                public void setX(int x) {
                    this.x = x;
                }

                public int getY() {
                    return y;
                }

                public void setY(int y) {
                    this.y = y;
                }
            }
        }
    }
}
