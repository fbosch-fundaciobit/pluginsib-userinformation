package org.fundaciobit.plugins.userinformation;

/**
 * 
 * @author anadal
 * 
 */
public class UserInfo {

  public enum Gender {
    MALE, FEMALE, UNKNOWN
  }

  String username;

  String administrationID;

  String name;

  String surname1;

  String surname2;

  String email;

  String language;

  String phoneNumber;
  
  String password;

  Gender gender = Gender.UNKNOWN;
  
  String address;
  
  String company;

  String website;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getAdministrationID() {
    return administrationID;
  }

  public void setAdministrationID(String administrationID) {
    this.administrationID = administrationID;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSurname1() {
    return surname1;
  }

  public void setSurname1(String surname1) {
    this.surname1 = surname1;
  }

  public String getSurname2() {
    return surname2;
  }

  public void setSurname2(String surname2) {
    this.surname2 = surname2;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public Gender getGender() {
    return gender;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getWebsite() {
    return website;
  }

  public void setWebsite(String website) {
    this.website = website;
  }
  
  
  public String getFullName() {
    StringBuffer str = new StringBuffer();
    if (this.getName() != null) {
      str.append(name);
    }
    if (this.getSurname1() != null) {
      if (str.length() != 0) {
        str.append(' ');
      }
      str.append(this.getSurname1());
    }
    
    if (this.getSurname2() != null) {
      if (str.length() != 0) {
        str.append(' ');
      }
      str.append(this.getSurname2());
    }
    return str.toString();
  }

  @Override
  public String toString() {
    return username + " - " + administrationID + " - " + name + " " + surname1 
        + " " + surname2 + " - " + email + " - " + language;
  }

}
