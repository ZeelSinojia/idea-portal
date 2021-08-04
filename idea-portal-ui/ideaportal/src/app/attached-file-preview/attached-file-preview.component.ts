import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-attached-file-preview',
  templateUrl: './attached-file-preview.component.html',
  styleUrls: ['./attached-file-preview.component.scss']
})
export class AttachedFilePreviewComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

  link_to_all_documents: string = "#"

  files: string[] = [
    'favicon.ico',
    'assets/file/img1.jpg',
    'assets/file/img2.jpg',
    'assets/file/img3.jpeg',
    'assets/file/img4.jpg',
    'assets/file/temp-doc.png'
  ];

  imgSupported: string[] = ['jpg', 'png', 'jpeg'];
  docSupported: string[] = ['doc', 'txt', 'pdf'];

  allCnt: number = this.files.length;
  filesCnt: number = 0;
  fileIcon: string = '';

  fileMap = new Map<string, string>();
  extension: string | undefined = '';

  extSelector(ext: string, path: string) {
    if (this.imgSupported.includes(ext)) {
      this.filesCnt++;
      return path;
    }
    else if (this.docSupported.includes(ext)) {
      this.filesCnt++;
      return 'assets/file/temp-doc.png';
    }
    else {
      return '';
    }
  }

  fileTypeSolver() {
    for (var limit: number = 0; limit < this.files.length; limit++) {

      if (this.filesCnt >= 4) {
        break;
      }

      this.extension = this.files[limit].split('.').pop();
      if (this.extension == undefined) {
        continue
      }
      this.fileIcon = this.extSelector(this.extension, this.files[limit])

      if (this.fileIcon == '') {
        continue
      }
      this.fileMap.set(this.fileIcon, this.files[limit])
    }
  }

  getFiles() {
    this.fileTypeSolver()
    return this.fileMap;
  }

  getName(val: string) {
    return val.split('/').pop()
  }
}
