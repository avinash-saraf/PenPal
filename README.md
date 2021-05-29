# PenPal
A social media chat app for android that allows people to connect and meet with others who have similar interests/hobbies.
A key feature of PenPal is that all users would be completely anonymous with regards to not setting a profile photo and biography. This would help create more ‘authentic’ matches based purely on similarity in hobbies. <br>
### **Development is still in progress** <br>
Some features have not been implemented yet. For more details on those features, read "Features yet to be Implemented".

## Features
- A user can sign-up/login using email adress or a mobile number.
- A user can select his interests/hobbies from a comprehensive list of the same, by swiping right to select the interests and swiping left to deselect the interests. The user's chosen interests can be edited easily. All the users selected interests/hobbies are displayed. Currently, users can remove their selected interests by navigating to that particular interest under the specific category and swiping left.
- A user can connect and private chat with other users who share his interests/hobbies.
- A user can accept or ignore an incoming chat request.
- A user can private chat by sending text messages and images.
- A user can see if his contacts are online in the private chat page. If they are not online, the last time the contact was online will be displayed.
- A user can find all his contacts in the contacts tab, and remove contacts as well.

## Feature Implementation
- Sign-Up/Login using email address or a mobile number using firebase authentication as the backend.
- Selection of interests/hobbies
  - TabLayout activity with ViewPager2 and SectionsPagerAdapter. A placeholder fragment with RecyclerView is used for each different category of interests/hobbies to minimize code. Categories -> Indoor, Outdoor and Academic. All the hobbies of each category are saved locally in .txt files (hobby name and image url), retrieved and displayed in Cards using RecyclerView.
  - A separate fragment shows the interests already selected using FirebaseRecyclerView.
  - Interests/hobbies can be selected by swiping right, and deselected by swiping left on the respective hobby (technically a card using CardView). The selected hobbies then get added to FireBaseDatabase under the specific user's node (set as the unique id instantiated by firebase auth) in the 'interests' node. Similar for deselection of hobbies.
  - A check mark is also shown when the user selects a particular hobby. A floating action button appears when the user removes a particular hobby, allowing the user to undo that action.
- Main Activity/Page
  - BottomNavigationView using fragments for displaying Chats, adding a new contact, displaying all contacts, and displaying incoming/outgoing chat requests.
- Private chatting
  - Messages sent b/w two users by saving each message on Firebase Realtime Database for each user (messages node -> sender user's unique id -> reciever user's unique id) and displaying using RecyclerView. Automatic scroll to last message sent in the chat activity.
  - Images can be sent as well. Images are displayed using url of image location in Firebase Storage. Images are compressed to 30% quality before saving in Firebase Storage.
  - Online Status/Last time seen of reciever is displayed as well.
- Adding New Contacts
  - Each user node is iterated through from the start, user having atleast one hobby in common is selected, and chat request is sent to the same user. Provided that the user is not the same user or already added as a contact.
  - If no user with similar hobby is found, search using each category of interests/hobby is performed and user is selected. 
  - Each contact is displayed in the contacts page using FirebaseRecyclerView. <br>
### Features yet to be implemented
- Interests/hobbies in the 'selected' tab can be removed directly without navigating and deleting from the other tabs.
