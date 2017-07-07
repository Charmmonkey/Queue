const functions = require('firebase-functions');

  // // Create and Deploy Your First Cloud Functions
  // // https://firebase.google.com/docs/functions/write-firebase-functions
  //
  // exports.helloWorld = functions.https.onRequest((request, response) => {
  //  response.send("Hello from Firebase!");
  // });

  const admin = require('firebase-admin');
  admin.initializeApp(functions.config().firebase);

//  exports.sendNotification = functions.database.ref('').onWrite(event => {
//      const roomId = event.params.roomId;
//      const trackId = event.params.trackId;
//
//      console.log(userName,' added', songAdded, ' to the playlist!');
//
//      const getSongAddedPromise = admin.database().ref('/${roomId}/tracks/${trackId}/name').once('child_added');
//      const getSongAddedPromise = admin.database().ref('/${roomId}/tracks/${trackId}/author').once('child_added');
//
//
//      return Promise.all([getSongAddedPromise]).then(results => {
//          const songAdded = results[0];
//
//          // Notification Details
//          const payload = {
//              notification: {
//                  title: '',
//                  body: '',
//                  icon: ''
//              }
//          }
//      })
//
//  })

exports.makeUppercase = functions.database.ref('/{pushId}/messages/{messageId}')
    .onWrite(event => {
      // Grab the current value of what was written to the Realtime Database.
      const original = event.data.child("text").val();
      console.log('Uppercasing', event.params.messageId, original);
      const uppercase = original.toUpperCase();
      // You must return a Promise when performing asynchronous tasks inside a Functions such as
      // writing to the Firebase Realtime Database.
      // Setting an "uppercase" sibling in the Realtime Database returns a Promise.
      return event.data.ref.parent.child('uppercase').set(uppercase);
    });