# Privacy statement for Task Organiser
## Overview
This is a simple app to organise tasks and tick them off as they are done.  It is aimed at independent supported living for users with memory difficulties.

The tasks can be edited and grouped into a tree of sub-tasks (actions).  Optionally, an SMS can be sent to a supporting adult to inform them that a task has been completed, for example leaving the house to go shopping, catching a bus and returning.

## Stored data
The tasks can be created, edited and deleted within the app. Tey are stored internally within the app data in an un-encrypted json file.
Optionally, a user name and telephone number may be added for caring support adult to send selected task completion SMS messages.  The user name and telephone number are also stored internally un-encrypted with the app data.

## Permissions
### SEND_SMS
If a task is selected to send an SMS then it will be sent to the stored phone number, and a short message will also appear to the user indicating this occurred.
There is also an option to export and import
### READ_EXTERNAL_STORAGE and WRITE_EXTERNAL_STORAGE
The user can optionally save or load json data storing the tasks to Download folder on the phone.  This can serve as a permanent backup, or potentially to share task lists.
