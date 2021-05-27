# PenPal
A social media chat app for android that allows people to connect and meet with others who have similar interests/hobbies.
A key feature of PenPal is that all users would be completely anonymous with regards to not setting a profile photo and biography. This would help create more ‘authentic’ matches based purely on similarity in hobbies. 

## Features
- A user can sign-up/login using email adress or a mobile number.
- A user can select his interests/hobbies from a comprehensive list of the same. The user's chosen interests can be edited easily.
- A user can connect and private chat with other users who share his interests/hobbies.
- A user can accept or ignore an incoming chat request.
- A user can private chat by sending text messages and images.
- A user can see if his contacts are online in the private chat page. If they are not online, the last time the contact was online will be displayed.
- A user can find all his contacts in the contacts tab, and remove contacts as well.

## Implementation of Features
- Sign-Up/Login using email address or a mobile number using firebase authentication as the backend.
- Selection of interests/hobbies
  - TabLayout activity with ViewPager2 and SectionsPagerAdapter. A placeholder fragment with RecyclerView is used for each different category of interests/hobbies to minimize code. All the hobbies of each categored are saved locally in .txt files (hobby name and image url), retrieved and displayed in Cards using RecyclerView.
  - Interests/hobbies can be selected by swiping right, and deselected by swiping left on the respective hobby (technically a card using CardView). The selected hobbies then get added to FireBaseDatabase under the specific user's node (set as the unique id instantiated by firebase auth) in the 'interests' node. Similar for deselection of hobbies.
  - A check mark is also shown when the user selects a particular hobby. A floating action button appears when the user adds or removes a particular hobby, allowing the user to undo that action.
  
