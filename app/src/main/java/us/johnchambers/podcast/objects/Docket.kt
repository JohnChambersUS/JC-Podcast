package us.johnchambers.podcast.objects

/**
 * Created by johnchambers on 3/13/18.
 *
 * Holds info about what to play in player.
 *
 * A docket is a document that lists the contents of a package
 *
 */
abstract class Docket(id : String) {

    lateinit var _docketType : String
    var _id = id

    open fun getId() : String {
        return _id
    }

}