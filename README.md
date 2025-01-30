# 🏦 Financial bank application

## Financial bank is an app that deals with importation and analysis of possible fraud transactions.

<br>

# 💡 About

* It is divided in separate modules who are able to communicate amongst themselves by use of kafka messaging.

* It uses a MySQL database to store information of users and transactions

* It accepts transactions in two formats, csv and xml.

To be able to make imports, the user is required to be logged-in, for showcase purposes there is a default user,
in case a user does not have an account, sign-up is possible, which will require email verification from the user,
but that requires a .env file with ENV_EMAIL and ENV_PASSWORD [setup](https://github.com/Corygoncrg/financial.bank/edit/master/README.md#--setup).

After logging in, the user will be able to go to the import page, and start making imports, or check for suspect transactions.

<br>

# ⬇️ Download

To download and use this project, it is only necessary to install Docker desktop, and run the project inside a container

| Docker |  Project
| :-: | :-: |
https://www.docker.com/products/docker-desktop/ | https://github.com/Corygoncrg/financial.bank/archive/refs/heads/master.zip

<br>

# 📝  Setup

There is one necessary step for the users module to be runnable, that is to create a .env file in the same directory as the docker-compose.yaml

Inside that .env file you will add and fill the necessary fields:
* ENV_EMAIL=
* ENV_PASSWORD=

It will look like this:

![image](https://github.com/user-attachments/assets/144045f3-1289-4643-ba00-735290441d7a)

the email field is filled with your email.

The password field must be filled with an app password. Here is a link for the Google support regarding app passwords and how to create them. 
https://support.google.com/mail/answer/185833?hl=en

*If you are not using gmail, look for the corresponding service from your email provider.

Once you are done setting up the .env file, you can run the docker-compose.yaml, the frontend page is http://localhost:5500 by default.

<br>

# 📋 Notes

| Default User  | Password
| :-: | :-:
Admin | 123999

*The creation of the default user can be modified by going the following directory "/users/src/main/resources/db/migration", and changing or deleting the "V1.1__add-test-user-to-table-users.sql" file.
