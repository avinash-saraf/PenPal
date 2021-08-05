# PenPal
A social media chat app for android that allows people to connect and meet with others who have similar interests/hobbies.
A key feature of PenPal is that all users would be completely anonymous with regards to not setting a profile photo and biography. This would help create more ‘authentic’ matches based purely on similarity in hobbies. <br>

## Download and Installation
### APK
Application can be downloaded via the url: https://github.com/avinash-saraf/PenPal/releases/download/v1.0.0/PenPalApp.apk


## Features
- A user can sign-up/login using email adress or a mobile number.
- A user can select his interests/hobbies from a comprehensive list of the same, by swiping right to select the interests and swiping left to deselect the interests. The user's chosen interests can be edited easily. All the users selected interests/hobbies are displayed. Currently, users can remove their selected interests by navigating to that particular interest under the specific category and swiping left.
- A user can connect and private chat with other users who share his interests/hobbies.
- A user can accept or ignore an incoming chat request.
- A user can private chat by sending text messages and images.
- A user can see if his contacts are online in the private chat page. If they are not online, the last time the contact was online will be displayed.
- A user can find all his contacts in the contacts tab, and remove contacts as well.

## Implementation
The following is how some of the most important parts of the app was implemented.
- Sign-Up/Login using email address or a mobile number using firebase authentication as the backend.
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



