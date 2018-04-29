package us.johnchambers.podcast.Events.fragment

import us.johnchambers.podcast.database.PodcastTable

class OpenSubscribedDetailEvent(podcastTableRow : PodcastTable) {

    var podcast = podcastTableRow
}