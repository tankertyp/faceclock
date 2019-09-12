package com.pingfly.faceclock.model.request;

public class ChangePasswordRequest {

        private String newPassword;

        public ChangePasswordRequest(String oldPassword, String newPassword) {
            this.newPassword = newPassword;
        }


        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }

}
