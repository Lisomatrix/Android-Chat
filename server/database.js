const low = require('lowdb')
const FileSync = require('lowdb/adapters/FileSync')

class User {
    constructor() {
        this.id = '';
        this.name = '';
        this.email = '';
        this.password = '';
    }
}

class Message {
    constructor() {
        this.id = '';
        this.message = '';
        this.senderId = '';
        this.destinyId = '';
    }
}

class Token {
    constructor() {
        this.token = '';
        this.userId = '';
    }
}

class Database {

    constructor() {
        this.adapter = new FileSync('db.json');
        this.db = low(this.adapter);

        this.db.defaults({
            users: [],
            messages: [],
            tokens: []
        }).write();
    }

    getElement(table, query) {
        return this.db.get(table).find(query).value();
    }

    getElements(table) {
        return this.db.get(table);
    }

    getFilteredElements(table, filter) {
        return this.db.get(table).filter(filter).value();
    }

    setElement(table, element) {
        this.db.get(table)
        .push(element)
        .write();
    }

    updateElement(table, query, element) {
        this.db.get(table)
        .find(query)
        .assign(element)
        .write();
    }

    removeElement(table, query) {
        this.db.get(table)
        .remove(query)
        .write();
    }
}

module.exports = Database;