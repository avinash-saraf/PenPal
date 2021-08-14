# PenPal
A social media chat app for android that allows people to connect and meet with others who have similar interests/hobbies.
A key feature of PenPal is that all users would be completely anonymous with regards to not setting a profile photo and biography. This would help create more ‘authentic’ matches based purely on similarity in hobbies. <br>

## Download and Installation 
### APK
Application can be downloaded via the url: <br>
https://github.com/avinash-saraf/PenPal/releases/download/v1.0.0/PenPalApp.apk <br> <br>
__Please note: 'find friends' feature (overflow menu) in this version is ONLY for testing purposes and is not a feature the actual app.__

### Open in android studio
 1. __Clone the repository__ using `git clone https://github.com/avinash-saraf/PenPal.git` OR __Download ZIP__ of this project (under the 'Code' button on this page) and unzip to a folder.
 2. In android studio, go to __File -> Import Project__ then choose this project folder and then click __Next -> Finish__.


## Features
- A user can sign-up/login using email adress or a mobile number.
- A user can select his interests/hobbies from a comprehensive list of the same, by swiping right to select the interests and swiping left to deselect the interests. The user's chosen interests can be edited easily. All the users selected interests/hobbies are displayed. Currently, users can remove their selected interests by navigating to that particular interest under the specific category and swiping left.
- A user can send a chat request to other users who share his/her interests/hobbies.
- A user can accept or ignore an incoming chat request.
- A user can private chat (by sending text messages and images) with contacts.
- A user can see if his contacts are online in the private chat page. If they are not online, the last time the contact was online will be displayed.
- A user can find all his contacts in the contacts tab, and remove contacts as well.

## Implementation
The following is how some of the most important parts of the app was implemented.
- Sign-Up/Login using email address or a mobile number using firebase authentication as the backend.  Unique id generated for each user account by FirebaseAuth is used to save user info (such as account details, and messages sent) in Firebase Realtime Database. (user's node is set as unique id)
- Selection of interests/hobbies
  - TabLayout activity with ViewPager2 and SectionsPagerAdapter. A placeholder fragment with RecyclerView and PageViewModel is used for each different category of interests/hobbies to minimize code. Categories -> Indoor, Outdoor and Academic. All the hobbies of each category are saved locally in .txt files (hobby name and image url), retrieved and displayed in Cards using RecyclerView.
  - A separate fragment shows the interests already selected using FirebaseRecyclerView.
  - Interests/hobbies can be selected by swiping right, and deselected by swiping left on the respective hobby (technically a card using CardView). The selected hobbies then get added to FireBaseDatabase under the specific user's node (set as the unique id instantiated by firebase auth) in the 'interests' node. Similar for deselection of hobbies.
  - A check mark is also shown when the user selects a particular hobby. A floating action button appears when the user removes a particular hobby, allowing the user to undo that action.
- Main Activity/Page
  - BottomNavigationView using fragments for displaying Chats, adding a new contact, displaying all contacts, and displaying incoming/outgoing chat requests.
- Private chat
  - Messages sent b/w two users by saving each message on Firebase Realtime Database for each user (messages node -> sender/reciever user's unique id -> reciever/sender user's unique id) and displaying using RecyclerView. Automatic scroll to last message sent in the chat activity.
  - Images can be sent as well. Images are displayed using url of image location in Firebase Storage. Images are compressed to 30% quality before saving in Firebase Storage.
  - State (online/last time seen) of the reciever user will be displayed below their name in the private chat activity
  - Layout is auto-scrolled to the latest message.
  - All the contacts user has chatted with will be displayed on the "Chats" Tab.
  - Last message sent between users in private chat can be seen below the reciever user's name on the home page.
- Adding New Contacts
  - When a user selects a hobby/interest, their unique id is saved under Interests > interest type (indoor/outdoor/academic) > Interest Name(Ex: acting/acrobatics)
  - To add a new contact, one hobby/interest of the user is selected at random. Check if the user's interest was selected by other app users. If true, randomly choose another user who is not saved in the current user's contact list, and send friend request. In case there are no other app users with the same interest - which will not happen if the userbase is large enough - request user to try search feature again or add a popular interest to their list.
  - Each contact is displayed in the contacts page using FirebaseRecyclerView. <br>

## Screenshots
  Note: Due to resizing, the images may not reflect the true quality (for that, click on the images to open them in a new tab)<br><br>
  - Sign-in <br> <br>
    <img src="https://user-images.githubusercontent.com/82901399/129348939-0a110a0a-4b77-4e0d-90fe-f0e85b485d6c.png" alt="Main Sign-In Page" width="378" height="647">  <img src="https://user-images.githubusercontent.com/82901399/129348949-817d17e8-a763-4c85-9311-a6a7c70494d4.png" alt="Email Sign-in" width="378" height="647"> 
    <img src="https://user-images.githubusercontent.com/82901399/129348954-82ca0433-1df0-4f3d-82de-77c5d1b43aed.png" alt="Phone sign-in" width="378" height="647">
    <img src="https://user-images.githubusercontent.com/82901399/129348975-304a3dc6-53bb-4fa8-b4bd-5d9cc8368053.png" alt="Phone sign-in" width="378" height="647"> <br> <br>
  - Chats <br> <br>
    <img src="https://user-images.githubusercontent.com/82901399/129350151-facb6f16-0120-4863-b7c6-3e07060882a8.png" alt="Chats Tab" width="378" height="647"> <img src="https://user-images.githubusercontent.com/82901399/129353205-4fd64a2b-f88d-4153-8b30-36e2e7dd50a7.png" alt="Chat Activity" width="378" height="647"> <br> <br>
  - Find PenPal (GIF) <br> <br>
    <img src="https://media.giphy.com/media/Kbt1YyefLtdTRxm4tS/giphy.gif" alt="Find PenPal Gif" width="378" height="647"> <br> <br> 
  - Account Settings and Editing Interests/Hobbies <br> <br>
    <img src="https://user-images.githubusercontent.com/82901399/129353608-b7d164d3-49b3-4182-877d-24e66b9e1238.png" alt="Settings Activity" width="378" height="647">
    <img src="https://user-images.githubusercontent.com/82901399/129353627-aacf38f3-585c-42f0-b846-096a3bb78610.png" alt="Interests Page" width="378" height="647"> <br> <br>
    Adding and removing interest (GIF) <br> <br>
    <img src="https://media.giphy.com/media/pbNQuq2O3avLZ3zFTe/giphy.gif" alt="Interests Page" width="378" height="647"> <br> <br>
  - Viewing contacts <br> <br>
    <img src="https://user-images.githubusercontent.com/82901399/129358150-e5cf4a1b-6293-4c9b-ac4c-d4a4ac40cca8.png" alt="Contacts Tab" width="378" height="647"> <img src="https://user-images.githubusercontent.com/82901399/129358158-8f62f66d-23cd-41bc-a3f9-fedd809804df.png" alt="Profile Page" width="378" height="647"> <br> <br>
    Clicking "View Interests" <br> <br>
    <img src="https://user-images.githubusercontent.com/82901399/129358164-8a50e50e-ea46-42c7-a47a-32159a352e1f.png" alt="View Interests" width="378" height="647">
    

