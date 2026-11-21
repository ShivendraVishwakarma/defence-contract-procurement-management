export interface VendorEvaluation {
  id: number;
  requisitionId: number;
  requisitionNumber: string;
  vendorId: number;
  vendorCode: string;
  vendorName: string;
  evaluatedByUserId: number;
  evaluatedByName: string;
  score: number;
  recommendation: string;
  evaluatedAt: string;
}

export interface VendorOption { id: number; code: string; name: string; }
export interface RequisitionOption { id: number; requisitionNumber: string; title: string; }
export interface VendorEvaluationOptions {
  vendors: VendorOption[];
  requisitions: RequisitionOption[];
}
export interface VendorEvaluationRequest {
  requisitionId: number;
  vendorId: number;
  score: number;
  recommendation: string;
}
