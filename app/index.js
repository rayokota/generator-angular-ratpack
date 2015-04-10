'use strict';
var util = require('util'),
    path = require('path'),
    yeoman = require('yeoman-generator'),
    _ = require('lodash'),
    _s = require('underscore.string'),
    pluralize = require('pluralize'),
    asciify = require('asciify');

var AngularRatpackGenerator = module.exports = function AngularRatpackGenerator(args, options, config) {
  yeoman.generators.Base.apply(this, arguments);

  this.on('end', function () {
    this.installDependencies({ skipInstall: options['skip-install'] });
  });

  this.pkg = JSON.parse(this.readFileAsString(path.join(__dirname, '../package.json')));
};

util.inherits(AngularRatpackGenerator, yeoman.generators.Base);

AngularRatpackGenerator.prototype.askFor = function askFor() {

  var cb = this.async();

  console.log('\n' +
    '+-+-+-+-+-+-+-+ +-+-+-+-+-+-+-+ +-+-+-+-+-+-+-+-+-+\n' +
    '|a|n|g|u|l|a|r| |r|a|t|p|a|c|k| |g|e|n|e|r|a|t|o|r|\n' +
    '+-+-+-+-+-+-+-+ +-+-+-+-+-+-+-+ +-+-+-+-+-+-+-+-+-+\n' +
    '\n');

  var prompts = [{
    type: 'input',
    name: 'baseName',
    message: 'What is the base name of your application?',
    default: 'myapp'
  }];

  this.prompt(prompts, function (props) {
    this.baseName = props.baseName;

    cb();
  }.bind(this));
};

AngularRatpackGenerator.prototype.app = function app() {

  this.entities = [];
  this.resources = [];
  this.generatorConfig = {
    "baseName": this.baseName,
    "packageName": this.packageName,
    "entities": this.entities,
    "resources": this.resources
  };
  this.generatorConfigStr = JSON.stringify(this.generatorConfig, null, '\t');

  this.template('_generator.json', 'generator.json');
  this.template('_package.json', 'package.json');
  this.template('_bower.json', 'bower.json');
  this.template('bowerrc', '.bowerrc');
  this.template('Gruntfile.js', 'Gruntfile.js');
  this.copy('gitignore', '.gitignore');

  var gradleDir = 'gradle/wrapper/';
  this.mkdir(gradleDir);
  this.copy('build.gradle', 'build.gradle');
  this.copy('gradlew', 'gradlew');
  this.copy('gradlew.bat', 'gradlew.bat');
  this.copy('gradle/wrapper/gradle-wrapper.jar', gradleDir + 'gradle-wrapper.jar');
  this.copy('gradle/wrapper/gradle-wrapper.properties', gradleDir + 'gradle-wrapper.properties');

  var ratpackDir = 'src/ratpack/';
  var appDir = 'src/main/groovy/' + this.baseName;
  var modelsDir = appDir + '/models';
  var publicDir = ratpackDir + 'public/';
  this.mkdir(ratpackDir);
  this.mkdir(appDir);
  this.mkdir(modelsDir);
  this.mkdir(publicDir);
  this.template('src/ratpack/_ratpack.properties', ratpackDir + 'ratpack.properties');
  this.template('src/ratpack/_Ratpack.groovy', ratpackDir + 'Ratpack.groovy');
  this.template('src/main/groovy/_app/_Application.groovy', appDir + 'Ratpack.groovy');

  var publicCssDir = publicDir + 'css/';
  var publicJsDir = publicDir + 'js/';
  var publicViewDir = publicDir + 'views/';
  this.mkdir(publicCssDir);
  this.mkdir(publicJsDir);
  this.mkdir(publicViewDir);
  this.template('public/_index.html', publicDir + 'index.html');
  this.copy('public/css/app.css', publicCssDir + 'app.css');
  this.template('public/js/_app.js', publicJsDir + 'app.js');
  this.template('public/js/home/_home-controller.js', publicJsDir + 'home/home-controller.js');
  this.template('public/views/home/_home.html', publicViewDir + 'home/home.html');
};

AngularRatpackGenerator.prototype.projectfiles = function projectfiles() {
  this.copy('editorconfig', '.editorconfig');
  this.copy('jshintrc', '.jshintrc');
};
