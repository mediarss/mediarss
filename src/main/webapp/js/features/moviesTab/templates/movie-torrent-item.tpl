<div class='movie-item-icon-wrapper'>
    <img src="images/download-blue.png" width="20" height="20" class='movie-torrent-item-download-image'
         title="Download Torrent"/>
    <img src="images/pending.png" width="24" height="24" class='movie-torrent-item-scheduled-image'
         title="Torrent Scheduled for Download"/>
    <img src="images/complete.png" width="20" height="20" class='movie-torrent-item-downloaded-image'
         title="Torrent Was Already Downloaded"/>
</div>
<div class="movie-torrent-item-inner" title='{{{title}}}'>
    <span class='movie-torrent-title'>{{{title}}}</span>

    <div class='movie-torrent-sub-title'>Uploaded on {{dateFormat uploadedDate format="DD/MM/YYYY HH:mm"
        default='never'}} <span class='movie-torrent-size'>Size: {{size}} MB </span><span class='movie-torrent-scheduled-on'>, Scheduled on <span
                class='movie-torrent-scheduled-on-date'>{{dateFormat scheduledDate format="DD/MM/YYYY HH:mm" default='never'}}</span></span>
    </div>
</div>
