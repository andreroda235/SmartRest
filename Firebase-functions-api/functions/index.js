const functions = require('firebase-functions');
const express = require('express');
const cors = require('cors');
const admin = require('firebase-admin');


admin.initializeApp(functions.config().firebase);
const db = admin.firestore();
const userApp = express();

userApp.use(cors({origin: true}));

/*
//get example
userApp.get('/', (req, res) => {
    const snapshot = db.collection('users').get();

    let users = [];
    snapshot.forEach(doc => {
        let id = doc.id;
        let data = doc.data();

        users.push({id, ...data});
    })

    res.status(200).send(JSON.stringify(users));
});*/

userApp.get("/:id", async (req, res) => {
    const snapshot = await db.collection('users').doc(req.params.id).get();

    const userId = snapshot.id;
    const userData = snapshot.data();

    res.status(200).send(JSON.stringify({id: userId, ...userData}));
})

//post example
userApp.post('/', async (req,res) => {
    const user = req.body;

    await db.collection('users').add(user)

    res.status(201).send();
});

//put example
userApp.put("/:id", async (req,res) => {
    const body = req.body;

    await db.collection('users').doc(req.params.id).update(body);

    res.status(200).send();
})

//delete example
userApp.delete("/:id", async (req,res) => {
    await db.collection('users').doc(request.params.id).delete();

    res.status(200).send();
})


exports.user = functions.https.onRequest(userApp);


//on create trigger example
exports.onUserCreate = functions.firestore.document('users/{userId}').onCreate(async (snap, context) => {
    const values = snap.data();

    await db.collection('logging').add({description: `Email was sent to user with username: ${values.username}`});
})

//on update trigger example
exports.onUserUpdate = functions.firestore.document('users/{userId}').onUpdate(async (snap, context) => {
    const newValues = snap.after.data();

    const previousValues = snap.before.data();
    if(newValues.username !== previousValues.username){
        const snapshot = await db.collection('reviews').where('username', '=', previousValues.username).get();

        let updatePromises = [];
        snapshot.forEach(doc => {
            updatePromises.push(db.collection('reviews').doc(doc.id).update({username: newValues.username}));
        });

        await Promise.all(updatePromises);
    }
})

//on delete trigger example
exports.onPostDelete = functions.firestore.document('post/{postId}').onDelete(async (snap, context) => {
    const deletedPost = snap.data();

    let deletedPromises = [];
    const bucket = admin.storage().bucket();
    deletedPost.images.forEach(image => {
        deletedPromises.push(bucket.file(image).delete());
    });

    await Promise.all(deletedPromises);
})


//funcao para smart rest
exports.onTableUpdate = functions
    .firestore
    .document('Restaurants/{restaurantId}/tables/{tableId}').onUpdate( async (snap, context) => {
        const newValues = snap.after.data();
        const previousValues = snap.before.data();

        const tableId = snap.key.toString();
        const tableNr = tableId.charAt(tableId.length-1);

        const snapshot = await db.collection(snap.ref.toString()).get();
        const registrationTokens = snapshot.data().employees;

        var message;
        if(newValues.occupiedState !== previousValues.occupiedState && newValues.occupiedState == true){
            //send notification new clients

           message = {
               notification : {
                   title : 'New client!',
                   body : 'Table ' + tableNr,
               },
                data : {
                table : tableNr,
              },
               tokens : registrationTokens,
           };

        }else if(newValues.requestState !== previousValues.requestState){
            const state = newValues.requestState;

            message = {
                notification : {
                    title : 'New Request!',
                    body : 'Table ' + tableNr,
                },
                 data : {
                 table : tableNr,
                 request : state,
               },
                tokens : registrationTokens,
            };
        }

        let response = await admin.messaging().sendMulticast(message);
        console.log(response);

        //mais casos para voltar para vermelho

    })

/*
exports.helloWorld = functions.https.onRequest((request, response) => {
  functions.logger.info("Hello logs!", {structuredData: true});
  response.send("Hello from Firebase!");
});*/