import { Component } from '@angular/core';
import { NotifierService } from 'angular-notifier';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})

export class AppComponent {
  title = 'ideaportal';

  private notifier: NotifierService;

	/**
	 * Constructor
	 *
	 * @param {NotifierService} notifier Notifier service
	 */
	public constructor( notifier: NotifierService ) {
		this.notifier = notifier;
	}

  public showNotification( type: string, message: string ): void {
		this.notifier.notify( type, message );
	}

  ngOnInit(){
    
    }
}
