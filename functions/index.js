const functions = require('firebase-functions');

  // // Create and Deploy Your First Cloud Functions
  // // https://firebase.google.com/docs/functions/write-firebase-functions
  //
  // exports.helloWorld = functions.https.onRequest((request, response) => {
  //  response.send("Hello from Firebase!");
  // });

  const admin = require('firebase-admin');
  admin.initializeApp(functions.config().firebase);

//  exports.makeUppercase = functions.database.ref('/{pushId}/messages/{messageId}')
//      .onWrite(event => {
//        // Grab the current value of what was written to the Realtime Database.
//        const original = event.data.child("text").val();
//        console.log('Uppercasing', event.params.messageId, original);
//        const uppercase = original.toUpperCase();
//        // You must return a Promise when performing asynchronous tasks inside a Functions such as
//        // writing to the Firebase Realtime Database.
//        // Setting an "uppercase" sibling in the Realtime Database returns a Promise.
//        return event.data.ref.parent.child('uppercase').set(uppercase);
//      });

  exports.sendNotification = functions.database.ref('/{pushId}/users/{userId}').onWrite(event => {
        const pushId = event.params.pushId;
        console.log('pushId', pushId);

        const newUser = event.data.child('name').val();
        console.log('newest user', newUser);


        return loadUsers(pushId).then(users => {
            let tokens = [];
            for (let user of users){
                tokens.push(user.registrationToken);
                console.log('user registration token', user.registrationToken);
            }

            let payload = {
                notification: {
                    title: 'Firebase Notification',
                    body: 'hi',
                    sound: 'default'
                }
            };

            return admin.messaging().sendToDevice(tokens, payload);
        });
  });


  function loadUsers(pushId){
        var str1 = '/';
        var str3 = '/users';
        var path = str1.concat(pushId,str3);
        const usersReference = admin.database().ref(path);
        console.log('user path', path);
        console.log('userReference', usersReference);
        let defer = new Promise((resolve, reject) => {
            usersReference.once('value', (snap) => {
                let data = snap.val();
                console.log('snap values', snap.val());
                let users = [];
                for(var property in data){
                    users.push(data[property]);
                    console.log('users', data[property]);
                }
                resolve(users);
            }, (err) => {
                reject(err);
            });
        });
        return defer;

  }





