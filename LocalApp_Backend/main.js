var exp = require("express");
    app = exp(),
    db = require("mongoose"),
    bp = require("body-parser");
    passport = require("passport");
    localstrategy = require("passport-local");
    passportlocalmongoose = require("passport-local-mongoose");
    jwt = require('jsonwebtoken');


db.set('useUnifiedTopology',true);
db.connect("mongodb://localhost:27017/localtradingapp",{useNewUrlParser: true});


userschema = new db.Schema({
    username : String,
    password : String,
});

user_info_schema = new db.Schema({
    name : String,
    address : String,
    phonenumber : String,
    type :  String,
    loginid : String, 
    items : [{
        type: db.Schema.Types.ObjectID,
        ref: 'items'
    }]
 });

userschema.plugin(passportlocalmongoose);

item_schema = new db.Schema({
    item_name : String,
    item_price : Number,
    item_Image : String,
    item_sellerId : String,
    item_sellername : String
});

users =  db.model("User", userschema);
user_info = db.model("User_Info",user_info_schema);
items = db.model("items",item_schema);

passport.use(new localstrategy(users.authenticate()));
passport.serializeUser(users.serializeUser());
passport.deserializeUser(users.deserializeUser());

app.use(passport.initialize());
app.use(exp.json({limit: '50mb'}));
app.use(exp.urlencoded({limit: '50mb' , extended:true}));



//  middleware to Authorize use of different Routes
// 200 is code for success
// 404 is for usernot found
// 404 us used for other errors as well
function tokenverify(req,res,next){
    console.log("intokenverify1");
    try {
        token = req.headers.token;
        if(!token){
            return res.status(404).send();
        }
        decodedToken = jwt.verify(token, 'RANDOM_TOKEN_SECRET');
        userId = decodedToken.userId;
        if (!userId) {
            return res.status(404).send();
        } else {
            req.currentuserId =  userId;
            res.status(200);
            next();
        }
      } catch {
        res.status(404).send();
      }
}



// first Route not required to do anyhting
app.get("/",function(req,res){
});


//login route
app.post("/login",function(req, res,next){
    passport.authenticate('local', function(err, user, info) {
        if (err) { return next(err); }

        if (!user) { return res.status(404).send("usernotfound");}

        req.logIn(user, function(err) {
            if (err) { return next(err); }
            token = jwt.sign({ userId: user._id },'RANDOM_TOKEN_SECRET',{ expiresIn: '24h' });
            return res.status(200).json({tokens: token});
        });
      })(req, res, next);
});

// Signup Route
app.post("/signup",function(req,res){
    console.log(req.body);
    users.register(new users({username: req.body.username}),req.body.password,function(err, user){
        if(err){
            console.log(err);
            return res.status(404).send();
        }
        passport.authenticate("local")(req, res, function(){
            console.log("signedup")
            token = jwt.sign({ userId: user._id },'RANDOM_TOKEN_SECRET',{ expiresIn: '24h' });
            res.status(200).json({tokens: token});
        });
    });
});


// Signout Route
app.get("/signout",function(req,res){
    req.logout();
    res.status(200).send();
});


// route for verifying if the token and giving the user.
app.post("/token",tokenverify,function(req,res){
    users.findById(req.currentuserId,function(err,usr){
        if(!err){
            console.log("logged In");
            user_info.findOne({"loginid" : req.currentuserId},function(err,userfound){
                if(err){
                    res.status(404).send();
                }
                else{
                    console.log("sent username");
                    console.log(userfound.name);
                    res.status(200).send({name : userfound.name});
                }
            })
        }
        else{
            res.status(404).send();
        }
    });
});


// route to update userDetails
app.post("/update",tokenverify,function(req,res){
    console.log("inside update route");
    var updatedinfo = {
        name : req.body.fullname,
        address : req.body.address,
        phonenumber : req.body.phonenumber,
        type : req.body.costomer_type,
        loginid : req.currentuserId
    };
    user_info.findOne({"loginid": req.currentuserId}, function(err,data){
        if (!data){
            console.log("created");
            user_info.create(updatedinfo,function(err,info){
                if(err){
                    console.log(err);
                }
                else{
                    res.status(200).send({tokens:req.body.tokens});
                }
            });
        }
        else{
            console.log("updated");
            user_info.findByIdAndUpdate(data._id,updatedinfo,function(err,newdata){
                if(err){
                    console.log(err);
                }
                else{
                    res.status(200).send({tokens:req.body.tokens});
                }
            });
        }
    });
   
});


// Route for Adding Items
// 300 == Item was not added
app.post("/additem",tokenverify,function(req,res){
    console.log(req.body.name);
    var item_seller_name;
    user_info.findOne({loginid:req.currentuserId},function(err,output){
        if(err){
            console.log(err);
            res.status(300).send();
        }
        else{
            item_seller_name = output.name;
            console.log(item_seller_name);
            newitem = {
                item_name: req.body.name,
                item_price: req.body.price,
                item_Image: req.body.image,
                item_sellerId: req.currentuserId,
                item_sellername: item_seller_name
            }
            items.create(newitem,function(err,item){
                if(err){
                    console.log(err);
                    res.status(300).send();
                }
                else{
                    res.status(200).send(item._id);
                }
            });
        }
    });
});

app.get("/getitem",tokenverify,function(req,res){
    console.log("get item route");
    console.log(req.query.itemtoken);
    items.findById(req.query.itemtoken,function(err,item){
        if(err){
            console.log("err1");
            res.status(300).send("err");
        }
        else{
            user_info.findOne({"loginid": item.item_sellerId}, function(err,returned_user){
                if(err){
                    console.log("err2");
                    res.status(300).send("err usr");
                }
                else{
                    console.log("2reached");
                    tosendobj = {
                        item_name : item.item_name,
                        item_price : item.item_price,
                        item_Image : item.item_Image,
                        seller_name : returned_user.name,
                        seller_address : returned_user.address
                    };
                    res.status(200).send(tosendobj);
                }
            });
        }
    });
});

app.get("/listitem",tokenverify,function(req,res){
    console.log("in list all items");
    items.find({},function(err,data){
        if(err){
            res.status(404).send();
        }
        else{
            console.log("sentitems");
            res.status(200).send({item_list:data});
        }
    });
});

// Starting the Server
app.listen(3000,function(req,res){
    console.log("server started at port 3000");
});
