'use strict';
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();


exports.sendPowerNotification = functions.database.ref("/online").onWrite((change,context) => {
  console.log('Power event triggered');
    if (!change.before.exists()) {
        return;
    }
    const status = change.after.val();
    const onOff =  status ? "on": "off";

    const payload = {
        notification: {
            title: 'Electricity Monitor - Power status changed',
            body: `Your electricity is now ${onOff}`,
            sound: "default"
        }
    };

    const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24 //24 hours
    };
    console.log('Sending notifications');
   
    var db = admin.firestore();
  
    var usersRef = db.collection('users');
    usersRef.get().then(doc => {
     if (doc.empty) {
       console.log('No matching documents.');
       throw new Error("No such document!");
     }
     doc.forEach(doc => {
    console.log('Document data:', doc.data());

     console.log('Sending notifications');
    admin.messaging().sendToDevice(doc.data().firebaseToken,payload, options);
 
     });   
     return doc.data(); 
     
   })
   .catch(err => {
     console.log('Error getting document', err);
   });
 });

exports.motionDetectedNotification = functions.firestore
    .document('/motions/{motionId}')
     .onCreate((snap, context) => {

   var db = admin.firestore();
  
   var usersRef = db.collection('users');
   usersRef.get().then(doc => {
    if (doc.empty) {
      console.log('No matching documents.');
      throw new Error("No such document!");
    }
    doc.forEach(doc => {
      console.log('Document data:', doc.data());
      const payload = {
        notification: {
            title: 'Intruder Notifications',
            body: 'There is motion at your door',
            sound: "default"
        }
    };

    const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24 //24 hours
    };
    console.log('Sending notifications');
   admin.messaging().sendToDevice(doc.data().firebaseToken,payload, options);

    });   
    return doc.data(); 
    
  })
  .catch(err => {
    console.log('Error getting document', err);
  });
    

  });