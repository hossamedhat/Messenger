const functions = require("firebase-functions");

const admin =require('firebase-admin');
admin.initializeApp();


exports.notifyNewMessage=functions.firestore
        .document('chats/{chat}/messages/{message}')
        .onCreate((docSnapshot,context)=>{
            const message =docSnapshot.data();
            const recipientId=message['recipient'];
            const senderName=message['senderName'];
            
            return admin.firestore().doc('users/'+recipientId).get().then(userDoc =>{
                const registerationTokens=userDoc.get('registerationTokens')

                const notificationBody=(message['type_text'] === "TEXT") ? message['type_text']:"You received a new image message."
                const payload={
                    notification:{
                        title:senderName + "sent you a message",
                        body: notificationBody,
                        USER_NAME: senderName,
                    }
                }
                return admin.messaging().sendToDevice(registerationTokens,payload).then(response => {
                    const stillRegisteredTokens=registerationTokens
                    
                    response.results.forEach((result,index)=>{
                        const error = result.error
                        if (error) {
                            const failedRegistrationToken = registrationTokens[index]
                            console.error('blah', failedRegistrationToken, error)
                            if (error.code === 'messaging/invalid-registration-token'
                                || error.code === 'messaging/registration-token-not-registered') {
                                    const failedIndex = stillRegisteredTokens.indexOf(failedRegistrationToken)
                                    if (failedIndex > -1) {
                                        stillRegisteredTokens.splice(failedIndex, 1)
                                    }
                                }
                        }
                    })
                    return admin.firestore().doc("users/" + recipientId).update({
                        registrationTokens: stillRegisteredTokens
                    })
                })
            })
        })   
