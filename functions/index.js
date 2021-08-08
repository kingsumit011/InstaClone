const functions = require("firebase-functions");

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

const admin = require("firebase-admin");
const fs=require('fs');
const nodemailer = require('nodemailer');

admin.intializeApp();

const  gmailEmail = 'kinsumit011@gmail.com';
const gmailPassword ='King1234.';
const mailTransport = nodemailer.createTransport({
    service: 'gmail',
    auth: {
        user: gmailEmail,
        pass: gmailPassword,
    },
});
var htmlmail = fs.readFileSync("welcome.html","utf-8").toString();

exports.sendWelcomeEmail = functions.auth.user().onCreate((user) => {
    const recipent_email = user.email;
    const mailOptions = {
        from: '"sender name" <kingsumit011@gmail.com>',
        to: recipent_email,
        subject: 'Welcome to Insta Clone',
        html: htmlmail
    };
    try{
        mailTransport.sendMail(mailOptions);
        console.log('mail send');
    }catch(error){
        console.log('There was an error while sending the email:',error);
    }
    return null
});