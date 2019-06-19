package com.smart.car.ui.model;

import java.util.ArrayList;
import java.util.List;

public class GroupAndPlaceVo {
    private List<GroupBean> places = new ArrayList<>();

    public List<GroupBean> getPlaces() {
        return places;
    }

    public void setPlaces(List<GroupBean> places) {
        this.places = places;
    }

    public class GroupBean{
        private int group_id;
        private String group_name;
        private boolean isSelect = false;

        public boolean isSelect() {
            return isSelect;
        }

        public void setSelect(boolean select) {
            isSelect = select;
        }

        private List<PlaceBean> place = new ArrayList<>();

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

        public class PlaceBean{
            private int id;
            private String name;
            private boolean isSelect = false;

            public boolean isSelect() {
                return isSelect;
            }

            public void setSelect(boolean select) {
                isSelect = select;
            }

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
        }
    }
}
