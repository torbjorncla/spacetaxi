window.onload = function() {





    var canvas = {
        //w: window.innerWidth,
        w: window.innerWidth,
        h: window.innerHeight,
        xMax: window.innerWidth,
        xMin: 0,
        yMax: window.innerHeight,
        yMin: 0,
    };


    var meteor_img = ["meteor_grey", "meteor_grey", "meteor_brown", "meteor_brown", "meteor_brown"]

    Meteor = function(game, l1, l2, l3, l4) {
        Phaser.Sprite.call(this, game, game.world.randomX, game.world.randomY, meteor_img[game.rnd.integerInRange(0,4)])
        this.anchor.setTo(0.5, 0.5)
        this.scale.set(game.rnd.realInRange(0.8, 0.4))
        this.rotateSpeed = game.rnd.realInRange(0.5, 2)
        this.speed = game.rnd.realInRange(40, 90)
        this.targetX = game.world.randomX
        this.targetY = game.world.randomY
        this.landings = [l1, l2, l3, l4]
        game.physics.arcade.enable(this)
        game.add.existing(this)
    }

    Meteor.prototype = Object.create(Phaser.Sprite.prototype)
    Meteor.prototype.constructor = Meteor

    Meteor.prototype.update = function() {
        this.angle += this.rotateSpeed
        if(offGrid(this)) {
            this.targetX = game.world.randomX
            this.targetY = game.world.randomY
            game.physics.arcade.moveToXY(this, this.targetX, this.targetY, this.speed, 0);
        }
        var self = this
        this.landings.forEach(function(l) {
            if(game.physics.arcade.collide(self, l)) {
                this.targetX = game.world.randomX
                this.targetY = game.world.randomY
                game.physics.arcade.moveToXY(self, this.targetX, this.targetY, this.speed, 0);
            }
        })
    }

    Landing = function(game, x, y) {
        Phaser.Sprite.call(this, game, x, y, "landing");
        this.scale.set(1.4)
        game.physics.arcade.enable(this)

        this.body.immovable = true;

        game.add.existing(this)
    }

    Landing.prototype = Object.create(Phaser.Sprite.prototype)
    Landing.prototype.constructor = Landing

    Landing.prototype.update = function() {
    }

    Ship = function(game, img, angle,a1, a2, speed, startLanding, stopLanding) {
        Phaser.Sprite.call(this, game, startLanding.position.x, startLanding.position.y, img)
        this.anchor.setTo(a1, a2)
        this.scale.set(0.6)
        this.angle = angle
        this.speed = speed;
        this.startLanding = startLanding
        this.stopLanding = stopLanding
        game.physics.arcade.enable(this)
        game.add.existing(this)
    }

    Ship.prototype = Object.create(Phaser.Sprite.prototype)
    Ship.prototype.constructor = Ship

    Ship.prototype.update = function() {
        var x1 = this.stopLanding.position.x;
        var x2 = this.position.x;
        var y1 = this.stopLanding.position.y;
        var y2 = this.position.y;
        var self = this
        if(game.math.distance(x1,y1,x2,y2) < 10) {
            //switch pos
            var back = self.stopLanding;
            self.stopLanding = self.startLanding
            self.startLanding = back;
            game.physics.arcade.moveToXY(self, self.stopLanding.position.x, self.stopLanding.position.y, self.speed, 0);

            if(self.angle == 90) {
                self.anchor.setTo(1,0)
                self.angle = 270
            } else {
                self.anchor.setTo(0,1)
                self.angle = 90
            }
        }
    }

    var game = new Phaser.Game(canvas.w, canvas.h, Phaser.AUTO, '', {
        preload: preload,
        create: create,
        update: update
    });

    function preload() {
        game.load.image("meteor_brown", "img/meteorBrown_big2.png")
        game.load.image("meteor_grey", "img/meteorGrey_big1.png")
        game.load.image("background", "img/back.png")
        game.load.image("sputnik", "img/spaceBuilding_014.png")
        game.load.image("landing", "img/spaceBuilding_025.png");
        game.load.image("ship_blue", "img/playerShip1_blue.png");
        game.load.image("ship_green", "img/playerShip1_green.png");
    }

    var landing1_1 = {}
    var landing1_2 = {}
    var landing2_1 = {}
    var landing2_2 = {}

    var meteors = []

    var ship1 = {}
    var ship2 = {}

    function create() {
        game.add.tileSprite(0, 0, canvas.xMax, canvas.yMax, "background");
        game.physics.startSystem(Phaser.Physics.ARCADE);
        game.world.setBounds(0, 0, canvas.w, canvas.h);
        game.stage.backgroundColor = "#00000"

        var ay = Math.floor((canvas.yMax / 2) / 2);
        var by = canvas.yMax - ay

        landing1_1 = new Landing(game, 40, ay);
        landing2_1 = new Landing(game, 40, by);
        landing1_2 = new Landing(game, canvas.xMax - 120, ay)
        landing2_2 = new Landing(game, canvas.xMax - 120, by)

        for(i = 0; i < 10; i++) {
            var m = new Meteor(game, landing1_1, landing1_2, landing2_1, landing2_2)
            game.physics.arcade.moveToXY(m, m.targetX, m.targetY, m.speed, 0)
            meteors.push(m)
        }

        ship1 = new Ship(game, "ship_blue", 90, 0, 1, 120, landing1_1, landing1_2);
        game.physics.arcade.moveToXY(ship1, landing1_2.position.x, landing1_2.position.y, ship1.speed, 0);

        ship2 = new Ship(game, "ship_green", 270, 1, 0, 120, landing2_2, landing2_1);
        game.physics.arcade.moveToXY(ship2, landing2_1.position.x, landing2_1.position.y, ship2.speed, 0);
    }

    function update() {
        //Send and read from websocket

        var x1 = ship1.stopLanding.position.x;
        var x2 = ship1.position.x;
        var y1 = ship1.stopLanding.position.y;
        var y2 = ship1.position.y;
        game.debug.text("Ship1 Distance: " + game.math.distance(x1,y1,x2,y2), 100, 400);


        var x12 = ship2.stopLanding.position.x;
        var x22 = ship2.position.x;
        var y12 = ship2.stopLanding.position.y;
        var y22 = ship2.position.y;
        game.debug.text("Ship2 Distance: " + game.math.distance(x12,y12,x22,y22), 100, 420);

    }

    function offGrid(s) {
        var x = s.position.x
        var y = s.position.y
        if(x > canvas.xMax || canvas.xMin > x || y > canvas.yMax || canvas.yMin > y) {
            return true;
        } else {
            return false;
        }
    }
}
